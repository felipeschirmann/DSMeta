package com.devsuperior.dsmeta.repositories;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dsmeta.entities.Sale;

@DataJpaTest
public class SaleRepositoryTests {

    @Autowired
    private SaleRepository repository;

    private Pageable pageable;

    @BeforeEach
    void setUp() throws Exception {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    public void findSalesShouldReturnSalesWithinDateRangeOrderedByAmountDesc() {
        // Range from 2022-06-01 to 2022-06-15 (inclusive)
        LocalDate min = LocalDate.of(2022, 6, 1);
        LocalDate max = LocalDate.of(2022, 6, 15);

        Page<Sale> result = repository.findSales(min, max, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        
        // Based on import.sql there should be 7 sales in this date range
        Assertions.assertEquals(7, result.getTotalElements());

        List<Sale> sales = result.getContent();

        // Verify ordering: amount DESC
        // Max amount should be Padme (20751.0)
        Assertions.assertEquals(20751.0, sales.get(0).getAmount());
        Assertions.assertEquals("Padme", sales.get(0).getSellerName());

        // Second should be Kal-El (15608.0)
        Assertions.assertEquals(15608.0, sales.get(1).getAmount());
        Assertions.assertEquals("Kal-El", sales.get(1).getSellerName());

        // Last should be Logan (4255.0)
        Assertions.assertEquals(4255.0, sales.get(6).getAmount());
        Assertions.assertEquals("Logan", sales.get(6).getSellerName());

        // Validate that all dates are indeed within [min, max]
        for (Sale sale : sales) {
            Assertions.assertTrue(!sale.getDate().isBefore(min) && !sale.getDate().isAfter(max),
                    "Sale date " + sale.getDate() + " is outside requested interval");
        }
    }

    @Test
    public void findSalesShouldReturnEmptyPageWhenNoSalesInDateRange() {
        // A range in the future where no sales exist
        LocalDate min = LocalDate.of(2030, 1, 1);
        LocalDate max = LocalDate.of(2030, 12, 31);

        Page<Sale> result = repository.findSales(min, max, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
    }
}
