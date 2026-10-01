package com.matchalab.travel_todo_api.service;

import com.matchalab.travel_todo_api.model.GooglePlaceAutoCompleteResponse;
import com.matchalab.travel_todo_api.model.GooglePlaceData;
import jakarta.annotation.PostConstruct;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.google.places")
public class GooglePlaceClientService {

  @Setter private String apiKey;
  @Setter private String baseUrl;

  public GooglePlaceAutoCompleteResponse autocomplete(
      String input, String language, String type) {

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

      return response;
  }
}
