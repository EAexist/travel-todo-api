package com.matchalab.travel_todo_api.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile({ "production" })
public class GcpCredentialsConfig {

    @Value("${GCP_CREDENTIALS_JSON:}")
    private String gcpCredentialsJson;

    @PostConstruct
    public void initializeGcpCredentials() {
        if (!gcpCredentialsJson.isEmpty()) {
            try {
                // 1. 임시 파일 생성
                File tempFile = File.createTempFile("gcp-credentials", ".json");

                // 2. 환경 변수 값(JSON 내용)을 임시 파일에 쓰기
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(gcpCredentialsJson);
                }

                // 3. GOOGLE_APPLICATION_CREDENTIALS 시스템 속성 설정
                // 이는 JVM 레벨에서 ADC가 키 파일을 찾도록 합니다.
                System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", tempFile.getAbsolutePath());

                // 4. (선택 사항) 애플리케이션 종료 시 임시 파일 삭제 예약
                tempFile.deleteOnExit();

            } catch (IOException e) {
                // 로깅 및 오류 처리
                e.printStackTrace();
            }
        }
    }

}
