package com.matchalab.travel_todo_api.model.Reservation;

import com.matchalab.travel_todo_api.enums.AccomodationCategory;
import com.matchalab.travel_todo_api.model.Link;
import lombok.Builder;

import java.util.List;
@Builder
public record Accomodation(
    AccomodationCategory category,
    String title,
    String roomTitle,
    String location,
    int numberOfClient,
    String clientName,
    String checkinDateIsoString,
    String checkoutDateIsoString,
    String checkinStartTimeIsoString,
    String checkinEndTimeIsoString,
    String checkoutTimeIsoString,
    List<Link> links)
    implements ReservationDetail {}
