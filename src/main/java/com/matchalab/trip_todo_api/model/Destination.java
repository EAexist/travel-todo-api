package com.matchalab.trip_todo_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(indexes = {
        @Index(name = "idx_description", columnList = "description")
})
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private String description;
    private String countryISO;
    private String title;
    private String region;

    public Destination(Destination destination) {
        this.trip = destination.getTrip();
        this.description = destination.getDescription();
        this.countryISO = destination.getCountryISO();
        this.title = destination.getTitle();
        this.region = destination.getRegion();
    }
}
