package com.matchalab.travel_todo_api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.travel_todo_api.enums.TodoPresetType;
import com.matchalab.travel_todo_api.model.Flight.Airline;
import com.matchalab.travel_todo_api.model.Flight.Airport;
import com.matchalab.travel_todo_api.model.Todo.StockTodoContent;
import com.matchalab.travel_todo_api.model.Todo.TodoPreset;
import com.matchalab.travel_todo_api.repository.TodoPresetRepository;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
@RequiredArgsConstructor
// @Profile({ "!local", "local-init-data" })
public class DataLoader implements CommandLineRunner {
    @Autowired
    private TodoPresetRepository todoPresetRepository;
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

            initializeTodoPreset();
            log.info("[DataLoader] Saved TodoPresets");

        } catch (DataIntegrityViolationException ignore) {
            ignore.printStackTrace();
        }
    }

    private void initializeTodoPreset() {

        for (TodoPresetType todoPresetType : TodoPresetType.values()) {
            log.info(String.format("Inserting todoPresetType: %s", todoPresetType));
            if (!todoPresetRepository.existsByType(todoPresetType)) {
                todoPresetRepository.save(TodoPreset.builder().type(todoPresetType).build());
            }
        }
    }

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