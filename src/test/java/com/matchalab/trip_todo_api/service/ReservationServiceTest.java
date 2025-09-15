package com.matchalab.trip_todo_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.matchalab.trip_todo_api.model.DTO.AccomodationDTO;
import com.matchalab.trip_todo_api.model.DTO.ReservationImageAnalysisResult;
import com.matchalab.trip_todo_api.model.Flight.Flight;
import com.matchalab.trip_todo_api.model.Flight.FlightTicket;
import com.matchalab.trip_todo_api.model.mapper.TripMapper;
import com.matchalab.trip_todo_api.model.mapper.TripMapperImpl;

import jakarta.annotation.PostConstruct;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        TripMapperImpl.class
})
@ActiveProfiles({ "local", "local-init-data" })
public class ReservationServiceTest {

    @Autowired
    private TripMapper tripMapper;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    @PostConstruct
    public void setup() throws IOException {
        ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create();
        VisionService visionService = new VisionService(new CloudVisionTemplate(imageAnnotatorClient));
        // GenAIService genAIService = new GenAIService(
        // new VertexAiGeminiChatModel(new VertexAI(),
        // VertexAiGeminiChatOptions.builder().model("gemini-2.0-flash-lite").build(),
        // ToolCallingManager.builder().build(),
        // new RetryTemplate(), null));
        GenAIService genAIService = new GenAIService();
        reservationService.setVisionService(visionService);
        reservationService.setGenAIService(genAIService);
        tripMapper = new TripMapperImpl();
    }

    @Test
    void Given_AccomodationReservation_Agoda_IOSAppScreenshot_testUploadReservationImage() throws IOException {
        AccomodationDTO expectedAccomodationDTO = new AccomodationDTO(null, "호텔 PAQ 도쿠시마 (HOSTEL PAQ tokushima)",
                "도미토리 내 1인 예약 (혼성)", 1, "HYEONPYO", "2025-02-24", "2025-02-25", "15:00", null,
                "10:00", "도쿠시마", null, new HashMap<String, String>());
        String[] filePaths = { "/image/accomodation-agoda-app-ios_1.tiff", "/image/accomodation-agoda-app-ios_2.tiff" };
        List<MultipartFile> files = readFiles(filePaths);
        ReservationImageAnalysisResult ReservationImageAnalysisResult = reservationService
                .analyzeReservationScreenImage(
                        files);

        assertThat(ReservationImageAnalysisResult).isNotNull();
        assertThat(ReservationImageAnalysisResult.flight()).isEmpty();
        assertThat(ReservationImageAnalysisResult.flightTicket()).isEmpty();
        assertThat(ReservationImageAnalysisResult.accomodation()).isNotEmpty();
        assertThat(tripMapper.mapToAccomodationDTO(ReservationImageAnalysisResult.accomodation().getFirst()))
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes()
                .ignoringFields("id", "links")
                .isEqualTo(expectedAccomodationDTO);
    }
}
