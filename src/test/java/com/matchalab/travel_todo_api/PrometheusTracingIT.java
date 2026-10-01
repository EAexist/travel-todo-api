package com.matchalab.travel_todo_api;

import com.matchalab.travel_todo_api.config.PostgresTestContainerSupport;
import com.matchalab.travel_todo_api.model.GooglePlaceAutoCompleteResponse;
import com.matchalab.travel_todo_api.service.GooglePlaceClientService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
@WithMockUser
@EnableWebSecurity
@Slf4j
public class PrometheusTracingIT implements PostgresTestContainerSupport {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    GooglePlaceClientService googlePlaceClientService;

    @BeforeEach
    void setup() {
        Mockito.when(googlePlaceClientService.autocomplete(any(), any(), any())).thenReturn(
                GooglePlaceAutoCompleteResponse.builder().build()
        );
    }

    @Test
    void whenRequest_thenExposeHttpRequestDurationMetrics() {

        requestAutocomplete();
        String metrics = requestPrometheusMetrics();

        String httpServerRequestsSecondsBucketPrefix = "http_server_requests_seconds_bucket{error=\"none\",exception=\"none\",method=\"GET\",outcome=\"SUCCESS\",stage_id=\"none\",status=\"200\",uri=\"/proxy/place/autocomplete/json\"";
        int count = StringUtils.countOccurrencesOf(metrics, httpServerRequestsSecondsBucketPrefix);
        assertThat(count).isEqualTo(69);

        String minBucketMetric = "http_server_requests_seconds_bucket{error=\"none\",exception=\"none\",method=\"GET\",outcome=\"SUCCESS\",stage_id=\"none\",status=\"200\",uri=\"/proxy/place/autocomplete/json\",le=\"0.001\"}";
        String maxBucketMetric = "http_server_requests_seconds_bucket{error=\"none\",exception=\"none\",method=\"GET\",outcome=\"SUCCESS\",stage_id=\"none\",status=\"200\",uri=\"/proxy/place/autocomplete/json\",le=\"30.0\"}";
        String infBucketMetric = "http_server_requests_seconds_bucket{error=\"none\",exception=\"none\",method=\"GET\",outcome=\"SUCCESS\",stage_id=\"none\",status=\"200\",uri=\"/proxy/place/autocomplete/json\",le=\"+Inf\"}";
        assertThat(metrics).contains(minBucketMetric, maxBucketMetric, infBucketMetric);
    }

    @Test
    void whenRequestWithXStageTestHeader_thenExposeStageLabel() {

        try {
            mockMvc.perform(get("/proxy/place/autocomplete/json")
                            .param("input", "input")
                            .param("language", "language")
                            .param("type", "type")
                            .header("Load-Test-Stage-Id", "stage_0"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String metrics = requestPrometheusMetrics();
        String requestCountMetricWithStageLabel = "http_server_requests_seconds_count{error=\"none\",exception=\"none\",method=\"GET\",outcome=\"SUCCESS\",stage_id=\"stage_0\",status=\"200\",uri=\"/proxy/place/autocomplete/json\"}";
        assertThat(metrics).contains(requestCountMetricWithStageLabel);
    }

    // TODO: use this to do TDD with Cache Observability
//    @Test
    void whenRequest_thenExposeCacheRequestTotalMetric() {
        requestAutocomplete();
        assertPrometheusMetricsContains(
                "", "");
    }

    void requestAutocomplete() {
        try {
            mockMvc.perform(get("/proxy/place/autocomplete/json")
                            .param("input", "input")
                            .param("language", "language")
                            .param("type", "type"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String requestPrometheusMetrics() {
        try {
            return mockMvc.perform(get("/actuator/prometheus")
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            throw new AssertionError("Failed to execute GET /actuator/prometheus", e);
        }
    }

    void assertPrometheusMetricsContains(String... expectedSubStrings) {
        Matcher<String>[] matchers = Arrays.stream(expectedSubStrings)
                .map(Matchers::containsString)
                .toArray(Matcher[]::new);

        assertOnPrometheusMetrics(allOf(matchers));
    }

    void assertOnPrometheusMetrics(Matcher<? super String> matcher) {
        try {
            mockMvc.perform(get("/actuator/prometheus")
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(matcher));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
