package com.matchalab.travel_todo_api.service;

import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.exception.NotFoundException;
import com.matchalab.travel_todo_api.exception.TripNotFoundException;
import com.matchalab.travel_todo_api.jobs.AppSqsProperties;
import com.matchalab.travel_todo_api.load_test.StageContext;
import com.matchalab.travel_todo_api.mapper.ReservationMapper;
import com.matchalab.travel_todo_api.model.Reservation.Reservation;
import com.matchalab.travel_todo_api.model.Reservation.ReservationDTO;
import com.matchalab.travel_todo_api.model.Reservation.ReservationPatchDTO;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.travel_todo_api.repository.ReservationRepository;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.reservation_analysis.CreateReservation;
import com.matchalab.travel_todo_api.reservation_analysis.CreateReservationJob;
import com.matchalab.travel_todo_api.reservation_analysis.CreateReservationJobRepository;
import com.matchalab.travel_todo_api.reservation_analysis.CreateReservationPayload;
import com.matchalab.travel_todo_api.service.ChatModelService.ChatModelService;
import io.awspring.cloud.sqs.operations.MessagingOperationFailedException;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsOperations;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Setter
public class ReservationService {

    /*
     * Repository
     */
    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private final CreateReservationJobRepository createReservationJobRepository;
    /*
     * Service
     */
    @Autowired
    private ChatModelService chatModelService;

    @Autowired
    private final HtmlParserService htmlParserService;

    /*
     * Mapper
     */
    @Autowired
    private ReservationMapper reservationMapper;

    private final StageContext stageContext;
    private final MeterRegistry registry;

    /* SQS */
//    https://docs.awspring.io/spring-cloud-aws/docs/3.0.0/reference/html/index.html#sqs-template-send
    private final SqsOperations sqsTemplate;
    private final AppSqsProperties appSqsProperties;

    public ReservationService(
            TripRepository tripRepository,
            CreateReservationJobRepository createReservationJobRepository,
            HtmlParserService htmlParserService,
            MeterRegistry registry, StageContext stageContext, SqsOperations sqsTemplate, AppSqsProperties appSqsProperties) {
        this.tripRepository = tripRepository;
        this.createReservationJobRepository = createReservationJobRepository;
        this.htmlParserService = htmlParserService;
        this.stageContext = stageContext;
        this.registry = registry;
        this.sqsTemplate = sqsTemplate;
        this.appSqsProperties = appSqsProperties;
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    public List<ReservationDTO> getReservation(UUID tripId) {
        List<ReservationDTO> reservation =
                tripRepository
                        .findById(tripId)
                        .orElseThrow(() -> new TripNotFoundException(tripId))
                        .getReservations()
                        .stream()
                        .map(reservationMapper::mapToDTO)
                        .toList();
        return reservation;
    }
    public List<ReservationDTO> getReservations(List<UUID> ids) {
        return findAllReservationsById(ids).stream().map(it -> reservationMapper.mapToDTO(it)).toList();
    }

    public List<Reservation> findAllReservationsById(List<UUID> ids) {
        String stageId = stageContext.getCurrentStageId();

        Timer.Sample sample = Timer.start(registry);
        try {
            return reservationRepository.findAllById(ids);
        } finally {
            Timer timer = Timer.builder("reservation.repository.find_all_by_id.duration")
                    .description("ORM latency for findAllById operations")
                    .tag("stage_id", stageId)
                    .serviceLevelObjectives(
                            Duration.ofNanos(100_000), // le="0.0001"   (100us)
                            Duration.ofNanos(250_000), // le="0.00025"  (250us)
                            Duration.ofNanos(500_000), // le="0.0005"   (500us)
                            Duration.ofNanos(750_000), // le="0.00075"  (750us)
                            Duration.ofMillis(1),      // le="0.001"    (1ms)
                            Duration.ofMillis(2),      // le="0.002"    (2ms)
                            Duration.ofMillis(5),      // le="0.005"    (5ms)
                            Duration.ofMillis(10)      // le="0.01"     (10ms)
                    )
//                    .publishPercentileHistogram()
                    .register(this.registry);
            sample.stop(timer);
        }
    }

    /**
     * Create new todo.
     */
    @Transactional
    public ReservationDTO createReservation(UUID tripId, ReservationPatchDTO reservationDTO) {
        Reservation reservation = reservationMapper.mapToReservation(reservationDTO);

        Trip trip =
                tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        trip.addReservation(reservation);
        tripRepository.save(trip);

        return reservationMapper.mapToDTO(reservation);
    }

    @Transactional
    public List<ReservationDTO> createReservationBatch(UUID tripId, List<ReservationPatchDTO> reservationDTOs) {
        if (!tripRepository.existsById(tripId)) {
            throw new TripNotFoundException(tripId);
        }
        Trip tripProxy = tripRepository.getReferenceById(tripId);

        List<Reservation> reservations = reservationDTOs.stream()
                .map(dto -> {
                    Reservation reservation = reservationMapper.mapToReservation(dto);
                    reservation.setTrip(tripProxy);
                    reservation.setId(UUID.randomUUID());
                    return reservation;
                })
                .toList();
        List<Reservation> savedReservations = saveAllReservations(reservations);

        return savedReservations.stream().map(it -> reservationMapper.mapToDTO(it)).toList();
    }

    @Transactional
    public List<Reservation> saveAllReservations(List<Reservation> reservations) {
        Timer.Sample sample = Timer.start(registry);
        try {
            return reservationRepository.saveAllAndFlush(reservations);
        } finally {
            String stageId = stageContext.getCurrentStageId();
            Timer timer = Timer.builder("reservation.repository.save_all.duration")
                    .description("ORM latency for saveAll operations")
                    .tag("stage_id", stageId)
                    .serviceLevelObjectives(
                            Duration.ofNanos(100_000), // le="0.0001"   (100us)
                            Duration.ofNanos(250_000), // le="0.00025"  (250us)
                            Duration.ofNanos(500_000), // le="0.0005"   (500us)
                            Duration.ofNanos(750_000), // le="0.00075"  (750us)
                            Duration.ofMillis(1),      // le="0.001"    (1ms)
                            Duration.ofMillis(2),      // le="0.002"    (2ms)
                            Duration.ofMillis(5),      // le="0.005"    (5ms)
                            Duration.ofMillis(10)      // le="0.01"     (10ms)
                    )
//                    .publishPercentileHistogram()
                    .register(this.registry);
            sample.stop(timer);
        }
    }

    /**
     * Change contents/orderKey of reservation.
     */
    public ReservationDTO patchReservation(
            UUID reservationId, ReservationPatchDTO newReservationDTO) {

        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() -> new NotFoundException(reservationId));
        Reservation updatedReservation =
                reservationMapper.updateFromDto(newReservationDTO, reservation);

        ReservationDTO reservationDTO =
                reservationMapper.mapToDTO(reservationRepository.save(updatedReservation));
        return reservationDTO;
    }

    /**
     * Delete reservation.
     */
    public void deleteReservation(UUID reservationId) {
        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() -> new NotFoundException(reservationId));
        Trip trip = reservation.getTrip();
        trip.removeReservation(reservation);
        tripRepository.save(trip);
    }

    @Transactional
    public List<ReservationDTO> saveReservation(UUID tripId, List<Reservation> reservation)
            throws Exception {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(null));

        Boolean isAdded = trip.addReservation(reservation);

        trip = tripRepository.save(trip);

        if (isAdded) {
            return reservation.stream().map(reservationMapper::mapToDTO).toList();
        } else {
            throw new Exception();
        }
    }

    public void createReservationFromText(
            UUID tripId, CreateReservationDTO createReservationDTO
    ) throws RuntimeException {
        Instant e2eOperationStartTime = Instant.now();

        CreateReservationJob job = new CreateReservationJob(
                tripId,
                CreateReservationPayload.builder()
                        .category(createReservationDTO.category())
                        .confirmationText(createReservationDTO.confirmationText()).build()
        );

        UUID jobId = createReservationJobRepository.save(job).getId();

        CreateReservation message = new CreateReservation(jobId, e2eOperationStartTime);

        try {
            SendResult<CreateReservation> sendResult = this.sqsTemplate.send(appSqsProperties.getReservationAnalysisQueueName(), message);
        }
        catch (MessagingOperationFailedException e) {
            throw new RuntimeException("SQS 메세지 발행 실패", e);
        }
        return;

//        try {
//            String parsedConfirmationText =
//                    htmlParserService.extractTextAndLink(createReservationDTO.confirmationText());
//
//            ReservationCategory category;
//            try {
//                category =
//                        createReservationDTO.category() != null
//                                ? createReservationDTO.category()
//                                : ReservationCategory.UNKNOWN;
//            } catch (IllegalArgumentException e) {
//                category = ReservationCategory.UNKNOWN;
//            }
//
//            List<Reservation> reservations =
//                    extractReservationFromText(parsedConfirmationText, category);
//
//            reservationDTOs =
//                    saveReservation(tripId, reservations);
//        } finally {
//            String stageId = stageContext.getCurrentStageId();
//            Timer reservationAnalysisE2eTimer = Timer.builder("reservation.analysis.e2e.duration")
//                    .description("End-to-end reservation analysis duration")
//                    .tag("stage_id", stageId)
//                    .publishPercentileHistogram()
//                    .register(this.registry);
//            sample.stop(reservationAnalysisE2eTimer);
//        }
//        return reservationDTOs;
    }

    public List<Reservation> extractReservationFromText(
            String confirmationText, ReservationCategory category) throws Exception {

        List<Reservation> reservation = new ArrayList<Reservation>();

        ExtractReservationChatResultDTO chatResult =
                chatModelService.extractReservationFromText(confirmationText);

        reservation.addAll(
                chatResult.flightBookings().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(
                chatResult.flightTickets().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(
                chatResult.accomodations().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(
                chatResult.otherReservations().stream().map(reservationMapper::mapToReservation).toList());

        reservation.stream()
                .forEach(
                        r -> r.setRawText(chatResult.partOfTextAndLinksThatContainsReservationInformation()));

        return reservation;
    }
}
