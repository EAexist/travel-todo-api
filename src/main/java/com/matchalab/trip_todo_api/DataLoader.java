package com.matchalab.trip_todo_api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.trip_todo_api.enums.TodoPresetType;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Todo.StockTodoContent;
import com.matchalab.trip_todo_api.model.Todo.TodoPreset;
import com.matchalab.trip_todo_api.model.Todo.TodoPresetStockTodoContent;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.AirportRepository;
import com.matchalab.trip_todo_api.repository.DestinationRepository;
import com.matchalab.trip_todo_api.repository.FlightRouteRepository;
import com.matchalab.trip_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetRepository;
import com.matchalab.trip_todo_api.repository.TodoPresetStockTodoContentRepository;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
// @Profile({ "!local", "local-init-data" })
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StockTodoContentRepository stockTodoContentRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private AirlineRepository airlineRepository;
    @Autowired
    private TodoPresetRepository todoPresetRepository;
    @Autowired
    private DestinationRepository destinationRepository;
    @Autowired
    private FlightRouteRepository flightRouteRepository;
    @Autowired
    private TodoPresetStockTodoContentRepository todoPresetStockTodoContentRepository;
    // @Autowired
    // private TripRepository tripRepository;
    // @Autowired
    // private AccomodationRepository accomodationRepository;
    // @Autowired
    // private DestinationRepository destinationRepository;

    // @Value("${spring.datasource.url}")
    // private String url;

    // @Value("${spring.datasource.username}")
    // private String username;

    // @Value("${spring.datasource.password}")
    // private String password;

    @Autowired
    private final ResourceLoader resourceLoader;

    @Override
    // @Transactional
    public void run(String... args) {

        try {
            destinationRepository.deleteAll();
            flightRouteRepository.deleteAll();

            initializeTodoPreset();
            log.info("[DataLoader] Saved TodoPresets");

            // List<Airport> airports = new ArrayList<Airport>();
            // airports = readCsv();
            // airportRepository.saveAll(airports);
            // log.info("Saved: airports");

            // List<Airline> airlines = new ArrayList<Airline>();
            // airlines = readCsv_airline();
            // airlineRepository.saveAll(airlines);

        } catch (DataIntegrityViolationException ignore) {
            ignore.printStackTrace();
        }
    }

    private void initializeTodoPreset() {

        todoPresetRepository.saveAll(
                List.of(TodoPreset.builder().type(TodoPresetType.DEFAULT).build(),
                        TodoPreset.builder().type(TodoPresetType.DOMESTIC).build(),
                        TodoPreset.builder().type(TodoPresetType.FOREIGN).build(),
                        TodoPreset.builder().type(TodoPresetType.JAPAN).build()));
    }

    // private void initializeTodoPreset() {
    // List<StockTodoContent> stockTodoContent =
    // stockTodoContentRepository.saveAll(readStockTodoContentJson());
    // log.info("Saved: stockTodoContent");

    // TodoPreset defaultTodoPreset =
    // todoPresetRepository.save(TodoPreset.builder().title("기본").build());
    // log.info("Saved: defaultTodoPreset");

    // defaultTodoPreset.getTodoPresetStockTodoContents().addAll(stockTodoContent.stream().map(content
    // -> {
    // return
    // TodoPresetStockTodoContent.builder().todoPreset(defaultTodoPreset).stockTodoContent(content)
    // .isFlaggedToAdd(true).build();
    // }).toList());
    // log.info("Set: defaultTodoPreset.todoPresetStockTodoContent");

    // todoPresetRepository.save(defaultTodoPreset);
    // log.info("Saved: TodoPresetStockTodoContent");
    // }

    private List<Airport> readCsv() {
        String filePath = "classpath:/static/airports_sample.csv";
        Resource resource = resourceLoader.getResource(filePath);
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            HeaderColumnNameMappingStrategy<Airport> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Airport.class);

            CsvToBean<Airport> csvToBean = new CsvToBeanBuilder<Airport>(reader)
                    .withType(Airport.class)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<Airport>();
        }
    }

    public List<Airline> readCsv_airline() {
        String filePath = "classpath:/static/airlines_sample.csv";
        Resource resource = resourceLoader.getResource(filePath);
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            HeaderColumnNameMappingStrategy<Airline> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Airline.class);

            CsvToBean<Airline> csvToBean = new CsvToBeanBuilder<Airline>(reader)
                    .withType(Airline.class)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<Airline>();
        }
    }

    public static List<StockTodoContent> readStockTodoContentJson() {
        List<StockTodoContent> presets = new ArrayList<StockTodoContent>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            presets = mapper.readValue((new ClassPathResource("static/stockTodoContent.json")).getInputStream(),
                    new TypeReference<List<StockTodoContent>>() {
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return presets;
    }
}