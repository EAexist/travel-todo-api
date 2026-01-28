package com.matchalab.travel_todo_api.config;

import java.util.List;
import java.util.UUID;

import com.matchalab.travel_todo_api.enums.TodoCategory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.matchalab.travel_todo_api.DTO.DestinationDTO;
import com.matchalab.travel_todo_api.DTO.TodoContentDTO;
import com.matchalab.travel_todo_api.DTO.TodoDTO;
import com.matchalab.travel_todo_api.DTO.TripDTO;
import com.matchalab.travel_todo_api.DTO.TripSummaryDTO;
import com.matchalab.travel_todo_api.DTO.UserAccountDTO;
import com.matchalab.travel_todo_api.enums.AccomodationCategory;
import com.matchalab.travel_todo_api.model.Accomodation;
import com.matchalab.travel_todo_api.model.Destination;
import com.matchalab.travel_todo_api.model.Icon;
import com.matchalab.travel_todo_api.model.Link;
import com.matchalab.travel_todo_api.model.Trip;
import com.matchalab.travel_todo_api.model.Todo.CustomTodoContent;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.Todo;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;

@TestConfiguration
public class TestConfig {

    @Bean
    UserAccount userAccount() {
        return UserAccount.builder().id(UUID.randomUUID()).nickname("nickname").kakaoId("kakaoId").googleId("googleId")
                .build();
    }

    @Bean
    UserAccountDTO userAccountDTO() {
        return UserAccountDTO.builder().id(null).nickname("nickname").build();
    }

    @Bean
    Destination destination_tokushima() {
        return new Destination("도쿠시마", "JP", "시코쿠", "시코쿠");
    }

    @Bean
    DestinationDTO destinationDTO_tokushima() {
        return new DestinationDTO(null, "도쿠시마", "JP", "시코쿠", "시코쿠");
    }

    @Bean
    Destination destination_osaka() {
        return new Destination("오사카", "JP", "간사이", "");
    }

    @Bean
    DestinationDTO destinationDTO_osaka() {
        return new DestinationDTO(null, "오사카", "JP", "간사이", "");
    }

    @Bean
    Destination destination_kyoto() {
        return new Destination("교토", "JP", "간사이", "간사이");
    }

    @Bean
    DestinationDTO destinationDTO_kyoto() {
        return new DestinationDTO(null, "교토", "JP", "간사이", "간사이");
    }

    @Bean
    Destination[] destinations() {
        return new Destination[] { destination_tokushima(), destination_kyoto() };
    }

    @Bean
    DestinationDTO[] destinationDTOs() {
        return new DestinationDTO[] { destinationDTO_tokushima(), destinationDTO_kyoto() };
    }

    @Bean
    Accomodation[] accomodations() {
        return new Accomodation[] {
                Accomodation.builder()
                        .category(AccomodationCategory.DORMITORY)
                        .title("Hostel PAQ Tokushima")
                        .roomTitle("혼성 도미토리 내 베드")
                        .location("도쿠시마")
                        .numberOfClient(2)
                        .clientName("PYO HYEON")
                        .checkinDateIsoString("2025-02-20T00:00:00.001Z")
                        .checkoutDateIsoString("2025-02-22T00:00:00.001Z")
                        .checkinStartTimeIsoString("2025-07-01T18:00:00")
                        .checkinEndTimeIsoString("2025-07-01T21:00:00")
                        .checkoutTimeIsoString("2025-07-01T10:00:00")
                        .links(List.of(
                                Link.builder().provider("googleMap").url("https://maps.app.goo.gl/81rvb62d2LKrYPNV7")
                                        .build(),
                                Link.builder().provider("airbnb").url(
                                        "https://www.airbnb.co.kr/hotels/35388028?guests=1&adults=1&s=67&unique_share_id=be1c9ac3-c029-4927-a05e-efe2166f1903")
                                        .build()))
                        .build(),
                Accomodation.builder()
                        .category(AccomodationCategory.AIRBNB)
                        .title("Yoshiko 님의 숙소")
                        .roomTitle("혼성 도미토리 내 베드")
                        .location("나루토")
                        .numberOfClient(2)
                        .clientName("PYO HYEON")
                        .checkinDateIsoString("2025-02-23T00:00:00.001Z")
                        .checkoutDateIsoString("2025-02-24T00:00:00.001Z")
                        .checkinStartTimeIsoString("2025-07-01T17:00:00")
                        .checkinEndTimeIsoString("2025-07-01T21:00:00")
                        .checkoutTimeIsoString("2025-07-01T10:00:00")
                        .links(List.of(
                                Link.builder().provider("googleMap").url("https://maps.app.goo.gl/yGivrbvsiyPBDVyR8")
                                        .build(),
                                Link.builder().provider("airbnb")
                                        .url("https://www.airbnb.co.kr/rooms/12317142?viralityEntryPoint=1&s=76")
                                        .build()))
                        .build()
        };
    }

    // private List<AccomodationDTO> accomodationDTOs = new
    // ArrayList<AccomodationDTO>(
    // Arrays.asList(new AccomodationDTO[] {
    // new AccomodationDTO(
    // null,
    // "Hostel PAQ Tokushima",
    // "혼성 도미토리 내 베드",
    // 2,
    // "PYO HYEON",
    // "2025-02-20T00:00:00.001Z",
    // "2025-02-22T00:00:00.001Z",
    // "2025-07-01T18:00:00",
    // "2025-07-01T21:00:00",
    // "2025-07-01T10:00:00",
    // "도쿠시마",
    // "dorm",
    // Map.of(
    // "googleMap", "https://maps.app.goo.gl/81rvb62d2LKrYPNV7", "airbnb",
    // "https://www.airbnb.co.kr/hotels/35388028?guests=1&adults=1&s=67&unique_share_id=be1c9ac3-c029-4927-a05e-efe2166f1903")),
    // new AccomodationDTO(
    // null,
    // "Yoshiko 님의 숙소",
    // "",
    // 2,
    // "PYO HYEON",
    // "2025-02-23T00:00:00.001Z",
    // "2025-02-24T00:00:00.001Z",
    // "2025-07-01T17:00:00",
    // "2025-07-01T21:00:00",
    // "2025-07-01T10:00:00",
    // "나루토",
    // "airbnb",
    // Map.of(
    // "googleMap", "https://maps.app.goo.gl/yGivrbvsiyPBDVyR8", "airbnb",
    // "https://www.airbnb.co.kr/rooms/12317142?viralityEntryPoint=1&s=76"))
    // }));

    @Bean
    Todo stockTodo() {
        return Todo.builder().note("환전은 미리미리 할 것").stockTodoContent(stockTodoContent).build();
    }

    @Bean
    TodoDTO stockTodoDTO() {
        return TodoDTO.builder()
                .id(stockTodo().getId())
                .orderKey(0)
                .note("환전은 미리미리 할 것")
                .completeDateIsoString(null)
                .content(TodoContentDTO.builder().id(UUID.nameUUIDFromBytes("stockTodoContent-currency".getBytes()))
                        .isStock(true).category(TodoCategory.FOREIGN).type(
                                "currency")
                        .title("환전").icon(new Icon("💱")).build())
                .build();
    }

    StockTodoContent stockTodoContent = StockTodoContent.builder()
            .id(UUID.nameUUIDFromBytes("stockTodoContent-currency".getBytes()))
            .category(TodoCategory.FOREIGN)
            .type("currency").title("환전").icon(new Icon("💱")).build();

    @Bean
    Todo customTodo() {
        return Todo.builder().id(UUID.nameUUIDFromBytes("customTodoContent-camera".getBytes()))
                .orderKey(1).note(
                        "카메라 필름 챙겼는지 확인할 것")
                // .completeDateIsoString("2025-02-23T00:00:00.001Z")
                .customTodoContent(customTodoContent).build();
    }

    @Bean
    TodoDTO customTodoDTO() {
        return TodoDTO.builder()
                .id(customTodo().getId())
                .orderKey(1)
                .note("카메라 필름 챙겼는지 확인할 것")
                // .completeDateIsoString("2025-02-23T00:00:00.001Z")
                .content(customTodoContentDTO)
                .build();
    }

    CustomTodoContent customTodoContent = CustomTodoContent.builder()
            .id(UUID.nameUUIDFromBytes("customTodoContent-camera".getBytes())).category(TodoCategory.ELECTRONICS)
            .type("goods").title("필름카메라").icon(new Icon("📸")).build();

    TodoContentDTO customTodoContentDTO = TodoContentDTO.builder()
            .id(customTodoContent.getId())
            .isStock(false).category(TodoCategory.ELECTRONICS)
            .type("goods")
            .title("필름카메라").icon(new Icon("📸")).build();

    @Bean
    Trip trip() {
        return Trip.builder().id(UUID.nameUUIDFromBytes("trip-0".getBytes())).title(
                "Vaundy 보러 가는 도쿠시마 여행").startDateIsoString(
                        "2025-02-20T00:00:00.001Z")
                .endDateIsoString(
                        "2025-02-25T00:00:00.001Z")
                .build();
    }

    @Bean
    Trip tripHydrated() {

        Trip _trip = Trip.builder().id(trip().getId()).title(
                "Vaundy 보러 가는 도쿠시마 여행").startDateIsoString(
                        "2025-02-20T00:00:00.001Z")
                .endDateIsoString(
                        "2025-02-25T00:00:00.001Z")
                .todolist(List.of(new Todo[] { stockTodo(), customTodo() }))
                .build();

        _trip.addDestinations(List.of(destinations()));

        return _trip;
    }

    @Bean
    TripDTO tripDTO() {
        return TripDTO.builder()
                .id(trip().getId())
                .isInitialized(false)
                .title("Vaundy 보러 가는 도쿠시마 여행")
                .startDateIsoString("2025-02-20T00:00:00.001Z")
                .endDateIsoString("2025-02-25T00:00:00.001Z")
                .destinations(List.of(destinationDTOs()))
                .todolist(List.of(new TodoDTO[] { stockTodoDTO(), customTodoDTO() }))
                .build();
    }

    @Bean
    TripSummaryDTO tripSummaryDTO() {
        return TripSummaryDTO.builder()
                .id(trip().getId())
                .title(trip().getTitle())
                .startDateIsoString(trip().getStartDateIsoString())
                .endDateIsoString(trip().getEndDateIsoString())
                .destinationTitles(List.of(destination_tokushima().getTitle(), destination_kyoto().getTitle()))
                .build();
    }
}
