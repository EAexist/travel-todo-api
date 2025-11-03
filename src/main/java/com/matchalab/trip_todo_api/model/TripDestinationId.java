package com.matchalab.trip_todo_api.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripDestinationId implements Serializable {
    @Column(name = "trip_id")
    private UUID tripId;

    @Column(name = "destination_id")
    private UUID destinationId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TripDestinationId that = (TripDestinationId) o;
        return Objects.equals(tripId, that.tripId) &&
                Objects.equals(destinationId, that.destinationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, destinationId);
    }
}