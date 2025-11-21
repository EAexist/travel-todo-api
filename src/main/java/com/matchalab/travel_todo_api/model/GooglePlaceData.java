package com.matchalab.travel_todo_api.model;

import java.util.List;

public record GooglePlaceData(
        String description,
        String id,
        List<MatchedSubString> matched_substrings,
        String place_id,
        String reference,
        StructuredFormatting structured_formatting,
        List<Term> terms,
        List<String> types) {

    public record MatchedSubString(
            int length,
            int offset) {
    }

    public record Term(
            int offset,
            String value) {
    }

    public record StructuredFormatting(
            String description,
            String main_text,
            String secondary_text) {
    }
}