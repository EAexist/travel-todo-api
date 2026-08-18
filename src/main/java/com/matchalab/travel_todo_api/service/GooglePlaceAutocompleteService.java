package com.matchalab.travel_todo_api.service;

import com.matchalab.travel_todo_api.model.GooglePlaceAutoCompleteResponse;
import com.matchalab.travel_todo_api.model.GooglePlaceData;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.google.places")
public class GooglePlaceAutocompleteService {

  @Autowired private final Iso2DigitNationCodeDataCache iso2DigitNationCodeDataCache;
  @Setter private String apiKey;
  @Setter private String baseUrl;
  @Value("classpath:/static/allowed-nation-iso2Digit.txt")
  private Resource nationsFile;

  private Set<String> nationSet = Collections.emptySet();

  public GooglePlaceAutoCompleteResponse googlePlaceAutocomplete(
      String input, String language, String type) {

    List<GooglePlaceData> googlePlaceDataList = new ArrayList<GooglePlaceData>();
    String httpStatus;

    if (!containsHangul(input)) {
      httpStatus = HttpStatus.OK.name();
    } else {

      RestTemplate restTemplate = new RestTemplate();
      URI uri =
          UriComponentsBuilder.fromUriString(baseUrl)
              .queryParam("input", input)
              .queryParam("key", apiKey)
              .queryParam("language", language)
              .queryParam("type", type)
              // .queryParams(query)
              .build()
              .toUri();

      GooglePlaceAutoCompleteResponse response =
          restTemplate.getForObject(uri, GooglePlaceAutoCompleteResponse.class);

      if (response == null) {
        return GooglePlaceAutoCompleteResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
            .build();
      }

      googlePlaceDataList = response.predictions();

      googlePlaceDataList =
          googlePlaceDataList.stream()
              .filter(
                  googlePlaceData -> {
                    String iso2DigitNationCode =
                        iso2DigitNationCodeDataCache.getIso2DigitNationCodeByName(
                            googlePlaceData.terms().getLast().value());

                    if ((iso2DigitNationCode != null) && isNationAllowed(iso2DigitNationCode)) {
                      return true;
                    } else {
                      return false;
                    }
                  })
              .toList();

      httpStatus = response.status();
    }

    return GooglePlaceAutoCompleteResponse.builder()
        .predictions(googlePlaceDataList)
        .status(httpStatus)
        .build();
  }

  @PostConstruct
  public void loadNations() {
    if (!nationsFile.exists()) {
      System.err.println("CRITICAL ERROR: allowed-nation-iso2Digit.txt not found!");
      return;
    }

    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(nationsFile.getInputStream(), StandardCharsets.UTF_8))) {

      Set<String> loadedSet =
          reader
              .lines()
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
