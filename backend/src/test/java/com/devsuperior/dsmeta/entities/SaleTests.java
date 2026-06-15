package com.devsuperior.dsmeta.entities;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SaleTests {

    @Test
    public void saleEntityConstructorAndGettersSettersShouldWorkCorrectly() {
        // Test No-Args Constructor
        Sale sale1 = new Sale();
        Assertions.assertNull(sale1.getId());
        Assertions.assertNull(sale1.getSellerName());
        Assertions.assertNull(sale1.getVisited());
        Assertions.assertNull(sale1.getDeals());
        Assertions.assertNull(sale1.getAmount());
        Assertions.assertNull(sale1.getDate());

        // Test Setters and Getters
        Long id = 1L;
        String name = "Padme";
        Integer visited = 150;
        Integer deals = 80;
        Double amount = 25000.0;
        LocalDate date = LocalDate.of(2022, 5, 20);

        sale1.setId(id);
        sale1.setSellerName(name);
        sale1.setVisited(visited);
        sale1.setDeals(deals);
        sale1.setAmount(amount);
        sale1.setDate(date);

        Assertions.assertEquals(id, sale1.getId());
        Assertions.assertEquals(name, sale1.getSellerName());
        Assertions.assertEquals(visited, sale1.getVisited());
        Assertions.assertEquals(deals, sale1.getDeals());
        Assertions.assertEquals(amount, sale1.getAmount());
        Assertions.assertEquals(date, sale1.getDate());

        // Test All-Args Constructor
        Sale sale2 = new Sale(id, name, visited, deals, amount, date);
        Assertions.assertEquals(id, sale2.getId());
        Assertions.assertEquals(name, sale2.getSellerName());
        Assertions.assertEquals(visited, sale2.getVisited());
        Assertions.assertEquals(deals, sale2.getDeals());
        Assertions.assertEquals(amount, sale2.getAmount());
        Assertions.assertEquals(date, sale2.getDate());
    }
}
