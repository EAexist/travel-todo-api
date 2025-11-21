package com.matchalab.travel_todo_api.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class Iso2DigitNationCodeDataCache {

    private Map<String, String> nationCodeMap = Collections.emptyMap();

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    // ResourceLoader와 ObjectMapper를 주입받습니다.
    public Iso2DigitNationCodeDataCache(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    /**
     * 애플리케이션 초기화 시점에 JSON 파일을 읽어 메모리에 로드합니다.
     */
    @PostConstruct
    public void init() {
        try {
            // 1. resourceLoader를 사용하여 파일을 Resource 객체로 로드합니다.
            Resource resource = resourceLoader.getResource("classpath:/static/nationNameKr-to-iso2Digit.json");

            // 2. ObjectMapper를 사용하여 JSON을 Map<String, String>으로 변환합니다.
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };

            Map<String, String> loadedMap = objectMapper.readValue(resource.getInputStream(), typeRef);

            // 3. 외부에서 수정할 수 없도록 Unmodifiable Map으로 감싸 저장합니다.
            this.nationCodeMap = Collections.unmodifiableMap(loadedMap);

            System.out.println("국가 코드 Map 로딩 완료. 항목 수: " + this.nationCodeMap.size());

        } catch (IOException e) {
            // 파일 로딩 실패 시 애플리케이션 시작을 중단하고 오류를 던집니다.
            throw new RuntimeException("국가 코드 JSON 파일 로드 실패", e);
        }
    }

    /**
     * Map의 값을 읽는 공용 메서드입니다.
     */
    public String getIso2DigitNationCodeByName(String nationName) {
        // O(1) 시간 복잡도로 즉시 접근
        return nationCodeMap.get(nationName);
    }
}