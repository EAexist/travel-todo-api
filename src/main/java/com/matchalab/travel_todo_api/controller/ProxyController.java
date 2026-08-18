package com.matchalab.travel_todo_api.controller;

import com.matchalab.travel_todo_api.model.GooglePlaceAutoCompleteResponse;
import com.matchalab.travel_todo_api.service.GooglePlaceAutocompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy")
public class ProxyController {

  @Autowired GooglePlaceAutocompleteService googlePlaceAutocompleteService;

  /** Provide the details of an Trip with the given id. */
  @GetMapping("/place/autocomplete/json")
  public GooglePlaceAutoCompleteResponse googlePlaceAutocomplete(
      @RequestParam String input, @RequestParam String language, @RequestParam String type) {

    return googlePlaceAutocompleteService.googlePlaceAutocomplete(input, language, type);
  }
}
