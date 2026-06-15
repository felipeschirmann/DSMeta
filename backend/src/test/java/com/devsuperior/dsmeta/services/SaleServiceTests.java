package com.devsuperior.dsmeta.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTests {

    @InjectMocks
    private SaleService service;

    @Mock
    private SaleRepository repository;

    private Sale sale;
    private Page<Sale> page;
    private Pageable pageable;

    @BeforeEach
    void setUp() throws Exception {
        sale = new Sale();
        sale.setId(1L);
        sale.setSellerName("Anakin");
        sale.setVisited(100);
        sale.setDeals(50);
        sale.setAmount(15000.0);
        sale.setDate(LocalDate.now());

        pageable = PageRequest.of(0, 10);
        page = new PageImpl<>(List.of(sale));
    }

    @Test
    public void findSalesShouldReturnPageWhenDatesAreProvided() {
        String minDate = "2022-01-01";
        String maxDate = "2022-12-31";

        LocalDate expectedMin = LocalDate.parse(minDate);
        LocalDate expectedMax = LocalDate.parse(maxDate);

        when(repository.findSales(eq(expectedMin), eq(expectedMax), any(Pageable.class))).thenReturn(page);

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Anakin", result.getContent().get(0).getSellerName());

        verify(repository, times(1)).findSales(expectedMin, expectedMax, pageable);
    }

    @Test
    public void findSalesShouldReturnPageWithCalculatedDatesWhenDatesAreEmpty() {
        String minDate = "";
        String maxDate = "";

        LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDate expectedMin = today.minusDays(365);
        LocalDate expectedMax = today;

        // Argument captor to inspect dynamically generated dates in service
        ArgumentCaptor<LocalDate> minCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> maxCaptor = ArgumentCaptor.forClass(LocalDate.class);

        when(repository.findSales(minCaptor.capture(), maxCaptor.capture(), any(Pageable.class))).thenReturn(page);

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        // Validate that calculated date bounds align with our service requirements
        assertEquals(expectedMin, minCaptor.getValue());
        assertEquals(expectedMax, maxCaptor.getValue());

        verify(repository, times(1)).findSales(any(LocalDate.class), any(LocalDate.class), eq(pageable));
    }

    @Test
    public void findSalesShouldReturnPageWithCalculatedMinDateWhenOnlyMaxDateIsProvided() {
        String minDate = "";
        String maxDate = "2022-12-31";

        LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDate expectedMin = today.minusDays(365);
        LocalDate expectedMax = LocalDate.parse(maxDate);

        ArgumentCaptor<LocalDate> minCaptor = ArgumentCaptor.forClass(LocalDate.class);

        when(repository.findSales(minCaptor.capture(), eq(expectedMax), any(Pageable.class))).thenReturn(page);

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        assertNotNull(result);
        assertEquals(expectedMin, minCaptor.getValue());

        verify(repository, times(1)).findSales(any(LocalDate.class), eq(expectedMax), eq(pageable));
    }

    @Test
    public void findSalesShouldReturnPageWithCalculatedMaxDateWhenOnlyMinDateIsProvided() {
        String minDate = "2022-01-01";
        String maxDate = "";

        LocalDate today = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDate expectedMin = LocalDate.parse(minDate);
        LocalDate expectedMax = today;

        ArgumentCaptor<LocalDate> maxCaptor = ArgumentCaptor.forClass(LocalDate.class);

        when(repository.findSales(eq(expectedMin), maxCaptor.capture(), any(Pageable.class))).thenReturn(page);

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        assertNotNull(result);
        assertEquals(expectedMax, maxCaptor.getValue());

        verify(repository, times(1)).findSales(eq(expectedMin), any(LocalDate.class), eq(pageable));
    }
}
