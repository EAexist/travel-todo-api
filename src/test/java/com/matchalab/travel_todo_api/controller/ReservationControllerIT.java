//https://docs.docker.com/guides/testcontainers-java-aws-localstack/#write-tests-with-testcontainers
package com.matchalab.travel_todo_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.travel_todo_api.DTO.CreateReservationDTO;
import com.matchalab.travel_todo_api.config.MockReservationConfig;
import com.matchalab.travel_todo_api.config.PostgresTestContainerSupport;
import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.travel_todo_api.enums.ReservationCategory;
import com.matchalab.travel_todo_api.jobs.AppSqsProperties;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.TripRepository;
import com.matchalab.travel_todo_api.repository.UserAccountRepository;
import com.matchalab.travel_todo_api.reservation_analysis.CreateReservation;
import com.matchalab.travel_todo_api.utils.TestUtils;
import com.matchalab.travel_todo_api.utils.Utils;
import io.awspring.cloud.sqs.operations.MessagingOperationFailedException;
import io.awspring.cloud.sqs.operations.SqsOperations;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser
@Import({TestConfig.class, MockReservationConfig.class})
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class ReservationControllerIT implements PostgresTestContainerSupport {

  @Autowired private MockMvc mockMvc;
  @Autowired private TripRepository tripRepository;
  @Autowired private UserAccountRepository userAccountRepository;
  @Autowired private SqsOperations sqsOperations;
  @Autowired private Trip trip;

  private UUID tripId;

  static LocalStackContainer localStack = new LocalStackContainer(
          DockerImageName.parse("localstack/localstack:4.12.0"))
          .withEnv("LOCALSTACK_AUTH_TOKEN", System.getenv("LOCALSTACK_AUTH_TOKEN"))
          .withServices("sqs")
          .withReuse(true)
          ;
  static {
    localStack.start();
  }
  @Autowired private AppSqsProperties appSqsProperties;

    @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add(
            "spring.cloud.aws.region.static",
            () -> localStack.getRegion()
    );
    registry.add(
            "spring.cloud.aws.credentials.access-key",
            () -> localStack.getAccessKey()
    );
    registry.add(
            "spring.cloud.aws.credentials.secret-key",
            () -> localStack.getSecretKey()
    );
    registry.add(
            "spring.cloud.aws.sqs.endpoint",
            () -> localStack.getEndpoint().toString()
    );
  }

  @BeforeEach
  void setUp() throws IOException, InterruptedException {
    localStack.execInContainer(
            "awslocal",
            "sqs",
            "create-queue",
            "--queue-name",
            appSqsProperties.getReservationAnalysisQueueName()
    );
    String queueUrl = localStack.execInContainer(
            "awslocal",
            "sqs",
            "get-queue-url",
            "--queue-name",
            appSqsProperties.getReservationAnalysisQueueName()
    ).getStdout().trim();

    localStack.execInContainer(
            "awslocal",
            "sqs",
            "purge-queue",
            "--queue-url",
            queueUrl
    );

    tripRepository.deleteAll();
    userAccountRepository.save(new UserAccount());
    Trip savedTrip = tripRepository.save(new Trip(trip));
    tripId = savedTrip.getId();
  }

  @Autowired
  private ObjectMapper objectMapper;
  @Test
  void givenValidConfirmationText_whenCreateReservationFromTextLive_thenSendCreateReservationMessage() throws Exception {
    CreateReservationDTO createReservationDTO =
            TestUtils.createReservationDTOFromFile(
                    "text/flightTicket/eastarjet/kakao_text_ko.txt", ReservationCategory.UNKNOWN);

    mockMvc.perform(
                    post("/trip/{tripId}/reservation/analysis/text", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(Utils.asJsonString(createReservationDTO)))
            .andExpect(status().isAccepted());

    try {
      Message<CreateReservation> message = sqsOperations.receive(appSqsProperties.getReservationAnalysisQueueName(), CreateReservation.class).orElseThrow(() -> new AssertionError("Expected a message in SQS queue, but none was found."));
      CreateReservation payload = message.getPayload();
      assertThat(payload).isNotNull();
      assertThat(payload.jobId()).isNotNull();
    }
    catch(MessagingOperationFailedException e) {
      throw new AssertionError(e);
    }
  }
}
