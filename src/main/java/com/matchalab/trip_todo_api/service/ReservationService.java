package com.matchalab.trip_todo_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matchalab.trip_todo_api.DTO.TodoDTO;
import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.exception.NotFoundException;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.mapper.ReservationMapper;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.Reservation.ReservationDTO;
import com.matchalab.trip_todo_api.model.Reservation.ReservationPatchDTO;
import com.matchalab.trip_todo_api.model.Todo.Todo;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.trip_todo_api.repository.ReservationRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.service.ChatModelService.ChatModelService;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class ReservationService {
    /*
     * Repository
     */
    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /*
     * Service
     */

    @Autowired
    private ChatModelService chatModelService;
    @Autowired
    private VisionService visionService;

    /*
     * Mapper
     */
    @Autowired
    private ReservationMapper reservationMapper;

    // public ReservationService(TripRepository tripRepository, VisionService
    // visionService, ChatModelService chatModelService) {
    // this.tripRepository = tripRepository;
    // this.chatModelService = chatModelService;
    // this.visionService = visionService;
    // }

    /**
     * Provide the details of a Trip with the given id.
     */
    public List<ReservationDTO> getReservation(UUID tripId) {
        List<ReservationDTO> reservation = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId)).getReservations().stream()
                .map(reservationMapper::mapToDTO).toList();
        return reservation;
    }

    /**
     * Create new todo.
     */
    @Transactional
    public ReservationDTO createReservation(UUID tripId, ReservationPatchDTO reservationDTO) {
        Reservation reservation = reservationMapper.mapToReservation(reservationDTO);
        log.info(Utils.asJsonString(reservationMapper.mapToDTO(reservation)));

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        trip.addReservation(reservation);
        tripRepository.save(trip);

        return reservationMapper.mapToDTO(reservation);
    }

    /**
     * Change contents/orderKey of reservation.
     */
    public ReservationDTO patchReservation(UUID reservationId, ReservationPatchDTO newReservationDTO) {

        log.info("DTO:" + Utils.asJsonString(newReservationDTO));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(reservationId));
        Reservation updatedReservation = reservationMapper.updateFromDto(newReservationDTO, reservation);
        log.info("Updated:" + Utils.asJsonString(reservationMapper.mapToDTO(updatedReservation)));

        ReservationDTO reservationDTO = reservationMapper
                .mapToDTO(reservationRepository.save(updatedReservation));
        log.info("Saved:" + Utils.asJsonString(reservationDTO));
        return reservationDTO;
    }

    /**
     * Delete reservation.
     */
    public void deleteReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(reservationId));
        Trip trip = reservation.getTrip();
        trip.removeReservation(reservation);
        tripRepository.save(trip);

    }

    @Transactional
    public List<ReservationDTO> saveReservation(UUID tripId, List<Reservation> reservation) throws Exception {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(null));

        Boolean isAdded = trip.addReservation(reservation);

        trip = tripRepository.save(trip);

        log.info("[ReservationService.saveReservation] reservationDTOs="
                + Utils.asJsonString(reservation.stream().map(reservationMapper::mapToDTO).toList()));

        if (isAdded) {
            return reservation.stream().map(reservationMapper::mapToDTO).toList();
        } else {
            throw new Exception();
        }
    }

    public List<Reservation> extractReservationFromText(
            String confirmationText, ReservationCategory category) throws Exception {

        List<Reservation> reservation = new ArrayList<Reservation>();

        ExtractReservationChatResultDTO chatResult = chatModelService
                .extractReservationFromText(confirmationText);

        reservation.addAll(chatResult.flightBookings().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(chatResult.flightTickets().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(chatResult.accomodations().stream().map(reservationMapper::mapToReservation).toList());

        reservation.addAll(chatResult.otherReservations().stream().map(reservationMapper::mapToReservation).toList());

        reservation.stream()
                .forEach(r -> r.setRawText(chatResult.partOfTextAndLinksThatContainsReservationInformation()));

        return reservation;
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    // public Reservation setLocalAppStorageFileUri(UUID tripId, UUID reservationId,
    // String localAppStorageFileUri) {
    // Reservation reservation = reservationRepository.findById(reservationId)
    // .orElseThrow(() -> new TripNotFoundException(tripId));
    // reservation.setLocalAppStorageFileUri(localAppStorageFileUri);

    // return reservationRepository.save(reservation);
    // }

    /**
     * Create new empty trip.
     */
    // private List<Reservation> analyzeFlightTicketTextAndCreateReservation(String
    // tripId, List<String> text) {

    // /* Analyze Text with Generative AI */
    // // List<Reservation> reservation =
    // chatModelService.extractReservationfromText(text,
    // // "항공권 모바일 티켓");
    // List<Reservation> reservation = Arrays.asList(new Reservation[] {
    // new Reservation(dateTimeIsoString = "2025-06-29", "flightTicket", "항공권 모바일
    // 티켓", "") });

    // /* Save Data */
    // Reservation.ReservationBuilder savedResultBuilder = Reservation.builder();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));

    // reservation.stream().forEach(res -> {
    // res.setTrip(trip);
    // });
    // trip.getReservations().addAll(reservation);

    // return tripRepository.save(trip).getReservations().subList(-1 *
    // (reservation.size()), -1);
    // }

    // private List<String> extractTextfromImage(List<MultipartFile> files) {
    // List<String> reservationText = files.stream().map(multipartFile -> {
    // try {
    // BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
    // return new InputStreamResource(multipartFile.getInputStream());
    // } catch (Exception e) {
    // return null;
    // }
    // })
    // // .map(Utils::multipartFileToBufferedImage)
    // // .map(Utils::bufferedImageToTiffResource)
    // .map(visionService::extractTextfromImage)
    // .flatMap(List::stream)
    // .collect(Collectors.toList());

    // log.info(String.format("[extractTextfromImage] {}",
    // reservationText.toString()));
    // return reservationText;
    // }

    /**
     * Create new empty trip.
     */
    // public List<Reservation> analyzeFlightTicketAndCreateReservation(String
    // tripId,
    // List<MultipartFile> files) {

    // /* Extract Text from Image */
    // List<String> reservationText = extractTextfromImage(files);

    // /* Analyze Text with Generative AI */
    // List<Reservation> reservation = Arrays
    // .asList(new Reservation[] {
    // new Reservation("2025-06-29", ReservationCategory.FlightTicket, "항공권 모바일 티켓",
    // "인천 → 도쿠시마") });

    // /* Save Data */
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));
    // reservation.stream().forEach(res -> {
    // res.setTrip(trip);
    // });
    // trip.getReservations().addAll(reservation);

    // return tripRepository.save(trip).getReservations().subList(-1 *
    // (reservation.size()), -1);
    // }

    /**
     * Create new empty trip.
     */
    // public ReservationImageAnalysisResult uploadReservationText(
    // String text) {

    // return chatModelService.extractReservationfromText(Arrays.asList(new
    // String[] {
    // text }));
    // }

    /**
     * Create new empty trip.
     */
    // public ReservationImageAnalysisResult uploadReservationLink(
    // String url) {

    // return chatModelService.extractReservationfromText(Arrays.asList(new
    // String[] {
    // url }));
    // }

    /**
     * Create new empty trip.
     */
    // public ReservationImageAnalysisResult saveImageAnalysisResult(String tripId,
    // ReservationImageAnalysisResult reservationImageAnalysisResult) {

    // /* Save Data */
    // ReservationImageAnalysisResult.ReservationImageAnalysisResultBuilder
    // savedResultBuilder = ReservationImageAnalysisResult
    // .builder();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));

    // List<Accomodation> accomodation =
    // reservationImageAnalysisResult.accomodation();
    // // accomodation.stream().forEach(acc -> {
    // // acc.setTrip(trip);
    // // });
    // trip.getAccomodation().addAll(accomodation);
    // savedResultBuilder = savedResultBuilder
    // .accomodation(tripRepository.save(trip).getAccomodation().subList(-1 *
    // (accomodation.size()), -1));

    // List<Flight> flight = reservationImageAnalysisResult.flight();
    // // flight.stream().forEach(fl -> {
    // // fl.setTrip(trip);
    // // });
    // trip.getFlight().addAll(flight);
    // savedResultBuilder = savedResultBuilder
    // .flight(tripRepository.save(trip).getFlight().subList(-1 * (flight.size()),
    // -1));

    // return savedResultBuilder.build();
    // }
    /**
     * Create new empty trip.
     */
    // public ReservationImageAnalysisResult analyzeReservationScreenImage(
    // List<MultipartFile> files) {
    // return analyzeReservationScreenImage(files, ReservationCategory.General);
    // }

    // public List<Reservation> analyzeReservationScreenImage(
    // List<MultipartFile> files, ReservationCategory category) {

    // /* Extract Text from Image */
    // List<String> text = files.stream().map(multipartFile -> {
    // try {
    // byte[] fileBytes = multipartFile.getBytes();
    // File tempFile = File.createTempFile("temp_tiff", ".tiff");
    // try (FileOutputStream fos = new FileOutputStream(tempFile)) {
    // fos.write(fileBytes);
    // }
    // tempFile.deleteOnExit(); // 프로그램 종료 시 삭제
    // return new FileUrlResource(tempFile.toURI().toURL());
    // // BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
    // // return new InputStreamResource(multipartFile.getInputStream());
    // } catch (Exception e) {
    // return null;
    // }
    // })
    // // .map(Utils::multipartFileToBufferedImage)
    // // .map(Utils::bufferedImageToTiffResource)
    // .map(visionService::extractTextfromImage)
    // .flatMap(List::stream)
    // .collect(Collectors.toList());

    // log.info(String.format("[extractTextfromImage] reservationText=%s",
    // reservationText.toString()));

    // return extractReservationFromText(text, category);
    // // return createEntitiesFromImageAnalysisResult(tripId, reservationText);
    // }
}
