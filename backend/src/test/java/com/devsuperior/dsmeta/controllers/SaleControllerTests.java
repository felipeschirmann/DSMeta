package com.devsuperior.dsmeta.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.services.SaleService;
import com.devsuperior.dsmeta.services.SmsService;

@WebMvcTest(controllers = SaleController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
})
public class SaleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    @MockBean
    private SmsService smsService;

    private Sale sale;
    private PageImpl<Sale> page;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;

        sale = new Sale();
        sale.setId(existingId);
        sale.setSellerName("Kal-El");
        sale.setVisited(100);
        sale.setDeals(50);
        sale.setAmount(10000.0);
        sale.setDate(LocalDate.of(2022, 10, 15));

        page = new PageImpl<>(List.of(sale));
    }

    @Test
    public void findSalesShouldReturnPageOfSales() throws Exception {
        String minDate = "2022-01-01";
        String maxDate = "2022-12-31";

        when(saleService.FindSales(eq(minDate), eq(maxDate), any(Pageable.class))).thenReturn(page);

        ResultActions result = mockMvc.perform(get("/sales")
                .param("minDate", minDate)
                .param("maxDate", maxDate)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content[0].id").value(existingId));
        result.andExpect(jsonPath("$.content[0].sellerName").value("Kal-El"));
        result.andExpect(jsonPath("$.content[0].amount").value(10000.0));

        verify(saleService, times(1)).FindSales(eq(minDate), eq(maxDate), any(Pageable.class));
    }

    @Test
    public void notifySmsShouldReturnOkWhenIdExists() throws Exception {
        doNothing().when(smsService).sendSms(existingId);

        ResultActions result = mockMvc.perform(get("/sales/{id}/notification", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        verify(smsService, times(1)).sendSms(existingId);
    }

    @Test
    public void notifySmsShouldThrowExceptionWhenIdDoesNotExist() throws Exception {
        // Since there is no custom exception handler configured (like @ControllerAdvice),
        // the unhandled EntityNotFoundException is propagated out of the DispatcherServlet.
        doThrow(EntityNotFoundException.class).when(smsService).sendSms(nonExistingId);

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/sales/{id}/notification", nonExistingId)
                    .accept(MediaType.APPLICATION_JSON));
        });

        verify(smsService, times(1)).sendSms(nonExistingId);
    }
}
