package com.matchalab.trip_todo_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

/*
https://github.com/spring-attic/spring-cloud-gcp/blob/main/spring-cloud-gcp-vision/src/test/java/org/springframework/cloud/gcp/vision/CloudVisionTemplateTests.java
*/
@ExtendWith(MockitoExtension.class)
@ActiveProfiles({ "local", "local-init-data" })
// @TestPropertySource(properties = { "spring.config.location
// =classpath:application-dev.yml" })
// @EnabledIfSystemProperty(named = "it.vision", matches = "true")
public class VisionServiceTest {

    private VisionService visionService;

    @BeforeEach
    public void setupVisionTemplate() throws IOException {
        ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create();
        visionService = new VisionService(new CloudVisionTemplate(imageAnnotatorClient));
    }

    // @Test
    void testExtractTextFromPdf() {
    }

    // @Test
    void testExtractTextfromImage() throws IOException {
        MultipartFile file;
        File _file = new ClassPathResource("/image/accomodation-agoda-app-ios_1.tiff").getFile();
        file = new MockMultipartFile(_file.getName(), new FileInputStream(_file));
        List<String> text = visionService.extractTextfromImage(file.getResource());
        assertThat(text).contains("HOSTEL PAQ tokushima");
    }
}
