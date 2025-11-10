package com.matchalab.trip_todo_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.model.Reservation.Reservation;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        ReservationMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class ReservationMapperTest {

    @Autowired
    private ReservationMapper reservationMapper;

    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    // private final TripMapper reservationMapper =
    // Mappers.getMapper(TripMapper.class);

    @BeforeAll
    public void setUp() {
    }

    // @Test
    // void
    // mapToReservation_Given_ReservationDTO_When_mapped_Then_correctReservation() {

    // Reservation mappedReservation =
    // reservationMapper.mapToReservation(reservationDTO);
    // assertNotNull(reservationDTO);
    // assertNotNull(mappedReservation);
    // assertThat(mappedReservation).usingRecursiveComparison()
    // .ignoringFieldsOfTypes().ignoringFields().isEqualTo(Reservation);
    // }

    // @Test
    // void
    // mapToReservationDTO_Given_Reservation_When_mapped_Then_correctReservationDTO()
    // {
    // ReservationDTO mappedReservationDTO =
    // reservationMapper.mapToReservationDTO(reservation);
    // assertNotNull(reservation);
    // assertNotNull(mappedReservationDTO);
    // assertThat(mappedReservationDTO).usingRecursiveComparison().isEqualTo(reservationDTO);
    // }
}
