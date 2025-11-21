package com.matchalab.travel_todo_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.vision.CloudVisionTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisionService {

    // @Autowired
    // private final ResourceLoader resourceLoader;

    @Autowired
    private final CloudVisionTemplate cloudVisionTemplate;

    /**
     * This method downloads an image from a URL and sends its contents to the
     * Vision API for label
     * detection.
     *
     * @param imageUrl the URL of the image
     * @param map      the model map to use
     * @return a string with the list of labels and percentage of certainty
     * @throws com.google.cloud.spring.vision.CloudVisionException if the Vision API
     *                                                             call produces an
     *                                                             error
     */

    // public Accomodation extractAccomodationfromPdf(String pdfUrl) {
    // String text = extractTextfromImage(pdfUrl);
    // return new Accomodation();
    // }

    // public Accomodation extractAccomodationfromImage(String imageUrl) {
    // String text = extractTextfromImage(imageUrl);
    // return new Accomodation();
    // }

    public List<String> extractTextfromImage(Resource resource) {
        // List<String> text = cloudVisionTemplate.extractTextFromFile(resource,
        // "image/tiff");
        String result = cloudVisionTemplate.extractTextFromImage(resource);
        List<String> text = List.of(result);
        return text;
    }

    // public List<String> extractTextFromPdf(String pdfUrl) {
    // List<String> texts =
    // this.cloudVisionTemplate.extractTextFromPdf(this.resourceLoader.getResource(pdfUrl));
    // return texts;
    // }

}
