// package com.matchalab.trip_todo_api.model.Reservation;

// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;

// import com.matchalab.trip_todo_api.enums.ReservationCategory;
// import com.matchalab.trip_todo_api.model.Accomodation;
// import com.matchalab.trip_todo_api.model.Flight.Flight;

// import jakarta.annotation.Nullable;
// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToOne;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
// import lombok.Setter;

// @RequiredArgsConstructor
// @AllArgsConstructor
// @Entity
// @Getter
// @Setter
// @Builder
// public class ReservationDTO {

// @Id
// @GeneratedValue(strategy = GenerationType.UUID)
// private String id;

// // @ManyToOne(fetch = FetchType.LAZY)
// // @JoinColumn(name = "user_id")
// // Trip trip;

// String dateTimeISOString;
// ReservationCategory type;
// String title;
// String subtitle;

// String rawText;

// @Nullable
// String link;

// @Nullable
// String serverFileUri;

// @Nullable
// String localAppStorageFileUri;

// @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
// @Nullable
// Accomodation accomodation;

// @JdbcTypeCode(SqlTypes.JSON)
// @Nullable
// private Flight flight;

// public Reservation(
// String dateTimeISOString,
// ReservationCategory type,
// String title,
// String subtitle) {
// this.dateTimeISOString = dateTimeISOString;
// this.type = type;
// this.title = title;
// this.subtitle = subtitle;
// }
// }