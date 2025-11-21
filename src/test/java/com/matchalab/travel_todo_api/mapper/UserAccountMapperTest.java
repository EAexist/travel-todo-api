package com.matchalab.travel_todo_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.matchalab.travel_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.DTO.UserAccountDTO;r;
import com.matchalab.travel_todo_api.mapper.UserAccountMapper;
import com.matchalab.travel_todo_api.model.UserAccount.UserAccount;
import com.matchalab.travel_todo_api.repository.StockTodoContentRepository;
import com.matchalab.trip_todo_api.config.TestConfig;
import com.matchalab.trip_todo_api.mapper.TripMapperImpl;
import com.matchalab.trip_todo_api.mapper.UserAccountMapperImpl;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
// @ExtendWith(MockitoExtension.class)
@Import({ TestConfig.class })
@ContextConfiguration(classes = {
        UserAccountMapperImpl.class,
        TripMapperImpl.class
})
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class UserAccountMapperTest {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserAccount userAccount;

    @Autowired
    private UserAccountDTO userAccountDTO;

    @MockitoBean
    private StockTodoContentRepository stockTodoContentRepository;

    @MockitoBean
    private TodoMapper todoMapper;

    @InjectMocks
    private TripMapperImpl tripMapper;

    /*
     * https://velog.io/@gwichanlee/MapStruct-Test-Code-%EC%9E%91%EC%84%B1
     * https://www.baeldung.com/mapstruct
     */
    // private final TripMapper userAccountMapper =
    // Mappers.getMapper(TripMapper.class);

    @BeforeAll
    public void setUp() throws Exception {
    }

    @Test
    void mapToUserAccountDTO_Given_userAccountWithEmptyTrip_When_mapped_Then_correctUserAccountDTO() {
        UserAccountDTO mappedUserAccountDTO = userAccountMapper.mapToUserAccountDTO(userAccount);
        assertNotNull(mappedUserAccountDTO);
        assertNotNull(mappedUserAccountDTO.id());
        assertThat(mappedUserAccountDTO).usingRecursiveComparison().ignoringFields("id").isEqualTo(userAccountDTO);
    }
}
