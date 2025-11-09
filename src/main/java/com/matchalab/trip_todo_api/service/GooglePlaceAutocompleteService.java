package com.matchalab.trip_todo_api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.matchalab.trip_todo_api.model.GooglePlaceAutoCompleteResponse;
import com.matchalab.trip_todo_api.model.GooglePlaceData;
import com.matchalab.trip_todo_api.utils.Utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlaceAutocompleteService {

    @Autowired
    private final Iso2DigitNationCodeDataCache iso2DigitNationCodeDataCache;

    private final String GOOGLE_PLACES_API_KEY = "AIzaSyClYZkWHBqRmV-UUFclFZPx2MeVl8RgTlI";
    private final String GOOGLE_PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    @Value("classpath:/static/allowed-nation-iso2Digit.txt")
    private Resource nationsFile;

    private Set<String> nationSet = Collections.emptySet();

    public GooglePlaceAutoCompleteResponse googlePlaceAutocomplete(String input, String language, String type) {
        log.info(String.format("input=%s , language=%s", input, language));

        List<GooglePlaceData> googlePlaceDataList = new ArrayList<GooglePlaceData>();
        String httpStatus;

        if (!containsHangul(input)) {
            httpStatus = HttpStatus.OK.name();
        } else {

            RestTemplate restTemplate = new RestTemplate();
            URI uri = UriComponentsBuilder.fromUriString(GOOGLE_PLACES_API_BASE_URL)
                    .queryParam("input", input)
                    .queryParam("key", GOOGLE_PLACES_API_KEY)
                    .queryParam("language", language)
                    .queryParam("type", type)
                    // .queryParams(query)
                    .build()
                    .toUri();

            GooglePlaceAutoCompleteResponse response = restTemplate.getForObject(uri,
                    GooglePlaceAutoCompleteResponse.class);

            googlePlaceDataList = response.predictions();

            log.info(String.format("googlePlaceDataList size: %s", googlePlaceDataList.size()));
            log.info(String.format("googlePlaceDataList: %s", Utils.asJsonString(googlePlaceDataList)));

            googlePlaceDataList = googlePlaceDataList.stream().filter(googlePlaceData -> {
                String iso2DigitNationCode = iso2DigitNationCodeDataCache
                        .getIso2DigitNationCodeByName(googlePlaceData.terms().getLast().value());

                if ((iso2DigitNationCode != null) && isNationAllowed(iso2DigitNationCode)) {
                    return true;
                } else {
                    return false;
                }
            }).toList();

            httpStatus = response.status();
        }

        return GooglePlaceAutoCompleteResponse.builder().predictions(googlePlaceDataList).status(httpStatus)
                .build();
    }

    @PostConstruct
    public void loadNations() {
        if (!nationsFile.exists()) {
            System.err.println("CRITICAL ERROR: allowed-nation-iso2Digit.txt not found!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(nationsFile.getInputStream(), StandardCharsets.UTF_8))) {

            Set<String> loadedSet = reader.lines()
                    .map(String::trim) // Clean up any leading/trailing whitespace
                    .filter(line -> !line.isEmpty()) // Skip empty lines
                    .collect(Collectors.toCollection(HashSet::new));

            this.nationSet = Collections.unmodifiableSet(loadedSet); // Make it immutable
            System.out.println("Loaded " + this.nationSet.size() + " allowed strings.");

        } catch (IOException e) {
            System.err.println("Error reading allowed-nation-iso2Digit.txt: " + e.getMessage());
        }
    }

    /**
     * Public method to check if a string is in the set.
     * 
     * @param inputString The string to check.
     * @return true if the string is allowed, false otherwise.
     */
    private boolean isNationAllowed(String iso2DigitNationCode) {
        return nationSet.contains(iso2DigitNationCode);
    }

    private boolean containsHangul(String str) {
        String regex = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
        return str.matches(regex);
    }

}