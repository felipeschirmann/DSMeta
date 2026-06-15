package com.devsuperior.dsmeta.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SaleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L; // Real existing record in import.sql
        nonExistingId = 999L; // Non-existing record
    }

    @Test
    public void findSalesShouldReturnPagedSalesWhenDatesAreProvided() throws Exception {
        String minDate = "2022-06-01";
        String maxDate = "2022-06-15";

        ResultActions result = mockMvc.perform(get("/sales")
                .param("minDate", minDate)
                .param("maxDate", maxDate)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content").isArray());
        
        // Asserting the real total records matching this range in import.sql
        result.andExpect(jsonPath("$.totalElements").value(7));

        // High amount first: Padme (20751.0)
        result.andExpect(jsonPath("$.content[0].sellerName").value("Padme"));
        result.andExpect(jsonPath("$.content[0].amount").value(20751.0));
    }

    @Test
    public void findSalesShouldReturnPagedSalesWhenDatesAreEmpty() throws Exception {
        ResultActions result = mockMvc.perform(get("/sales")
                .param("minDate", "")
                .param("maxDate", "")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void notifySmsShouldReturnOkWhenIdExists() throws Exception {
        // Since Twilio credentials in the test profile are mock placeholders, 
        // the real call will hit our sandbox protection check in SmsService and complete successfully
        ResultActions result = mockMvc.perform(get("/sales/{id}/notification", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void notifySmsShouldThrowExceptionWhenIdDoesNotExist() throws Exception {
        // Without an exception handler configured, the EntityNotFoundException 
        // will be propagated out of the request execution thread
        Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/sales/{id}/notification", nonExistingId)
                    .accept(MediaType.APPLICATION_JSON));
        });
    }
}
