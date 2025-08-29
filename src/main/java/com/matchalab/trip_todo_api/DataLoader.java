package com.matchalab.trip_todo_api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchalab.trip_todo_api.model.Flight.Airline;
import com.matchalab.trip_todo_api.model.Flight.Airport;
import com.matchalab.trip_todo_api.model.Todo.PresetTodoContent;
import com.matchalab.trip_todo_api.repository.AirlineRepository;
import com.matchalab.trip_todo_api.repository.AirportRepository;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PresetTodoContentRepository presetTodoContentRepository;
    @Autowired
    private AirportRepository airportRepository;
    @Autowired
    private AirlineRepository airlineRepository;
    // @Autowired
    // private TripRepository tripRepository;
    // @Autowired
    // private AccomodationRepository accomodationRepository;
    // @Autowired
    // private DestinationRepository destinationRepository;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    private final ResourceLoader resourceLoader;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        try {
            presetTodoContentRepository.saveAll(readPresetJson());

            List<Airport> airports = new ArrayList<Airport>();
            airports = readCsv();
            airportRepository.saveAll(airports);

            List<Airline> airlines = new ArrayList<Airline>();
            airlines = readCsv_airline();
            airlineRepository.saveAll(airlines);

        } catch (DataIntegrityViolationException ignore) {
            ignore.printStackTrace();
        }
    }

    private List<Airport> readCsv() throws IOException {
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
        }
    }

    public List<Airline> readCsv_airline() throws IOException {
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
        }
    }

    public static List<PresetTodoContent> readPresetJson() throws Exception {
        List<PresetTodoContent> presets = new ArrayList<PresetTodoContent>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            presets = mapper.readValue((new ClassPathResource("static/todoPreset.json")).getInputStream(),
                    new TypeReference<List<PresetTodoContent>>() {
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return presets;
    }
}