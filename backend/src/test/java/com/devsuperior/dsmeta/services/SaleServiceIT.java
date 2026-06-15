package com.devsuperior.dsmeta.services;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmeta.entities.Sale;

@SpringBootTest
@Transactional
public class SaleServiceIT {

    @Autowired
    private SaleService service;

    private Pageable pageable;

    @BeforeEach
    void setUp() throws Exception {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void findSalesShouldReturnSalesWithinDateRangeOrderedByAmountDesc() {
        // Range from 2022-06-01 to 2022-06-15 (inclusive)
        String minDate = "2022-06-01";
        String maxDate = "2022-06-15";

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        
        // Asserting the real total records matching this range in import.sql
        Assertions.assertEquals(7, result.getTotalElements());

        List<Sale> sales = result.getContent();

        // High amount first: Padme (20751.0)
        Assertions.assertEquals(20751.0, sales.get(0).getAmount());
        Assertions.assertEquals("Padme", sales.get(0).getSellerName());

        // Last amount: Logan (4255.0)
        Assertions.assertEquals(4255.0, sales.get(6).getAmount());
        Assertions.assertEquals("Logan", sales.get(6).getSellerName());
    }

    @Test
    public void findSalesShouldReturnSalesWithDefaultRangeWhenDatesAreEmpty() {
        String minDate = "";
        String maxDate = "";

        Page<Sale> result = service.FindSales(minDate, maxDate, pageable);

        Assertions.assertNotNull(result);
        // Since the current local year is 2026, the default range (today minus 365 days) 
        // will calculate [2025-06-15 to 2026-06-15]. Since import.sql has seed data in 2021/2022, 
        // the returned page should be empty, confirming the correct time-based query behavior.
        Assertions.assertTrue(result.isEmpty());
        
        // Default range is today minus 365 days. 
        // Our test database has static seed data in 2021/2022, so the default range (relative to today) 
        // might return few or no records depending on "today's" date, but we assert the Page object is created correctly
        Assertions.assertNotNull(result.getContent());
    }
}
