package com.esPublico.kata.service;

import com.esPublico.kata.config.DataSourceConfig;
import com.esPublico.kata.model.Order;
import com.esPublico.kata.util.PriorityEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DBServiceTest {

    @Test
    void exportOrdersToCsv() throws Exception {
        // Precondición: debe haber datos en la tabla
        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setId(1234);
        order.setRegion("Sub-Saharan Africa");
        order.setCountry("South Africa");
        order.setItemType("Fruits");
        order.setSalesChannel("Offline");
        order.setPriority(PriorityEnum.M);
        order.setDate("7/27/2012");
        order.setShipDate("7/28/2012");
        order.setUnitsSold(1593);
        order.setUnitPrice(9.33);
        order.setUnitCost(6.92);
        order.setTotalRevenue(14862.69);
        order.setTotalCost(11023.56);
        order.setTotalProfit(3839.13);
        DBService.getInstance().insertBatch(List.of(order));

        Long duration = DBService.getInstance().exportOrdersToCsv();
        assertNotNull(duration);
        assertTrue(duration > 0);

        File file = new File("orders.csv");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @AfterEach
    void cleanup() {
        File file = new File("orders.csv");
        if (file.exists()) {
            file.delete();
        }

        try (Connection conn = DataSourceConfig.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM orders WHERE id = ?")) {
            ps.setInt(1, 1234);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
