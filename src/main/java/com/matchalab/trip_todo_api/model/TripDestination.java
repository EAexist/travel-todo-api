package com.matchalab.trip_todo_api.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "trip_destination")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDestination {

    @EmbeddedId
    @Builder.Default
    private TripDestinationId id = new TripDestinationId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("destinationId")
    @JoinColumn(name = "destination_id")
    private Destination destination;

    public TripDestination(TripDestination source, Trip newTrip, Destination newDestination) {
        this.id = new TripDestinationId(newTrip.getId(), newDestination.getId());

        // 💡 새로운 Trip 및 Destination 인스턴스를 참조하도록 설정
        this.trip = newTrip;
        this.destination = newDestination;
    }
}