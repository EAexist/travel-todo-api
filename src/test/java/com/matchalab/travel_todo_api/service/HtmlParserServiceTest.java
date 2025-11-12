package com.matchalab.travel_todo_api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class HtmlParserServiceTest {

    @InjectMocks
    private HtmlParserService htmlParserService;

    String agoda_gmail_html_html;
    String agoda_gmail_text;

    @BeforeAll
    public void setup() throws IOException {
        agoda_gmail_html_html = StreamUtils.copyToString(
                (new ClassPathResource("text/accomodation/agoda/gmail_html_ko_hostelPAQTokushima.txt"))
                        .getInputStream(),
                StandardCharsets.UTF_8);
        agoda_gmail_text = StreamUtils.copyToString(
                (new ClassPathResource("text/accomodation/agoda/gmail_text_ko_hostelPAQTokushima.txt"))
                        .getInputStream(),
                StandardCharsets.UTF_8);
    }

    @Test
    void TestExtractTextAndLink() throws IOException {

        String extractedTextAndLink = htmlParserService.extractTextAndLink(agoda_gmail_html_html);

        assertThat(extractedTextAndLink).isNotEmpty();

        log.info(String.format("[TestExtractTextAndLink] parsed result:\n%s", extractedTextAndLink));
    }

    @Test
    void TestExtractTextAndLink_TEXT() throws IOException {

        String extractedTextAndLink = htmlParserService.extractTextAndLink(agoda_gmail_text);

        assertThat(extractedTextAndLink).isNotEmpty();

        log.info(String.format("[TestExtractTextAndLink_TEXT] parsed result:\n%s", extractedTextAndLink));
    }
}
