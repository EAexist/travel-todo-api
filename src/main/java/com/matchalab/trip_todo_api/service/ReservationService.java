package com.matchalab.trip_todo_api.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amadeus.exceptions.NotFoundException;
import com.matchalab.trip_todo_api.enums.ReservationCategory;
import com.matchalab.trip_todo_api.exception.TripNotFoundException;
import com.matchalab.trip_todo_api.model.Trip;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;
import com.matchalab.trip_todo_api.model.genAI.ExtractReservationChatResultDTO;
import com.matchalab.trip_todo_api.model.mapper.ReservationMapper;
import com.matchalab.trip_todo_api.repository.ReservationRepository;
import com.matchalab.trip_todo_api.repository.TripRepository;
import com.matchalab.trip_todo_api.service.ChatModelService.ChatModelService;
import com.matchalab.trip_todo_api.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class ReservationService {

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VisionService visionService;

    @Autowired
    private ChatModelService chatModelService;

    @Autowired
    private ReservationMapper reservationMapper;

    // public ReservationService(TripRepository tripRepository, VisionService
    // visionService, ChatModelService chatModelService) {
    // this.tripRepository = tripRepository;
    // this.chatModelService = chatModelService;
    // this.visionService = visionService;
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

    public List<Reservation> saveReservation(String tripId, List<Reservation> reservation) throws Exception {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(null));

        Boolean isAdded = trip.getReservation().addAll(reservation);

        log.info("[ReservationService.saveReservation] " + Utils.asJsonString(trip.getReservation()));

        trip = tripRepository.save(trip);

        if (isAdded) {
            return trip.getReservation();
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

        // if (chatResult.flightBookings() != null) {
        // if (!reservation
        // .addAll(chatResult.flightBookings().stream().map(reservationMapper::mapToReservation).toList()))
        // throw new Exception();
        // }
        // if (chatResult.flightTickets() != null) {
        // if (!reservation
        // .addAll(chatResult.flightTickets().stream().map(reservationMapper::mapToReservation).toList()))
        // throw new Exception();
        // }
        // if (chatResult.accomodations() != null) {
        // if (!reservation
        // .addAll(chatResult.accomodations().stream().map(reservationMapper::mapToReservation).toList()))
        // throw new Exception();
        // }
        // if (chatResult.otherReservations() != null) {
        // if (!reservation
        // .addAll(chatResult.otherReservations().stream().map(reservationMapper::mapToReservation).toList()))
        // throw new Exception();
        // }

        reservation.stream()
                .forEach(r -> r.setRawText(chatResult.partOfTextAndLinksThatContainsReservationInformation()));
        return reservation;
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    public List<Reservation> getReservation(String tripId) {
        List<Reservation> reservation = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId)).getReservation();
        return reservation;
    }

    /**
     * Provide the details of a Trip with the given id.
     */
    public Reservation setLocalAppStorageFileUri(String tripId, String reservationId,
            String localAppStorageFileUri) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new TripNotFoundException(tripId));
        reservation.setLocalAppStorageFileUri(localAppStorageFileUri);

        return reservationRepository.save(reservation);
    }

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
    // new Reservation(dateTimeISOString = "2025-06-29", "flightTicket", "항공권 모바일
    // 티켓", "") });

    // /* Save Data */
    // Reservation.ReservationBuilder savedResultBuilder = Reservation.builder();
    // Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new
    // TripNotFoundException(tripId));

    // reservation.stream().forEach(res -> {
    // res.setTrip(trip);
    // });
    // trip.getReservation().addAll(reservation);

    // return tripRepository.save(trip).getReservation().subList(-1 *
    // (reservation.size()), -1);
    // }

    private List<String> extractTextfromImage(List<MultipartFile> files) {
        List<String> reservationText = files.stream().map(multipartFile -> {
            try {
                BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
                return new InputStreamResource(multipartFile.getInputStream());
            } catch (Exception e) {
                return null;
            }
        })
                // .map(Utils::multipartFileToBufferedImage)
                // .map(Utils::bufferedImageToTiffResource)
                .map(visionService::extractTextfromImage)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        log.info(String.format("[extractTextfromImage] {}", reservationText.toString()));
        return reservationText;
    }

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
    // trip.getReservation().addAll(reservation);

    // return tripRepository.save(trip).getReservation().subList(-1 *
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
}
