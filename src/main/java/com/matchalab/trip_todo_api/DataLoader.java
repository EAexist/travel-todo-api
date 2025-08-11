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
import com.matchalab.trip_todo_api.model.Airport;
import com.matchalab.trip_todo_api.model.PresetTodoContent;
import com.matchalab.trip_todo_api.repository.AirportRepository;
import com.matchalab.trip_todo_api.repository.PresetTodoContentRepository;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PresetTodoContentRepository presetTodoContentRepository;
    @Autowired
    private AirportRepository airportRepository;
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        try {
            presetTodoContentRepository.saveAll(readPresetJson());

            List<Airport> airports = new ArrayList<Airport>();
            // airports = readCsv("static/airports.csv");
            airports = readCsv();

            airportRepository.saveAll(airports);

        } catch (DataIntegrityViolationException ignore) {
            ignore.printStackTrace();
        }
    }

    @Autowired
    private ResourceLoader resourceLoader;

    private List<Airport> readCsv() throws Exception {
        String filePath = "classpath:/static/airports.csv";
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