package com.esPublico.kata.service;

import com.esPublico.kata.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DBService {

    private final String sql;
    private final DataSource dataSource;
    private final DateTimeFormatter formatter;

    public DBService(DataSource dataSource) throws SQLException {
        this.sql = "INSERT INTO orders ( id, region, country, item_type, sales_channel, priority, order_date, ship_date, units_sold, unit_price, unit_cost, total_revenue, total_cost, total_profit ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        this.dataSource = dataSource;
        this.formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        /*this.conn = dataSource.getConnection();
        this.ps = conn.prepareStatement(sql);*/
    }

    public void insertBatch(List<Order> lista) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String ids = "";
            for (Order obj : lista) {
                ids = ids+obj.getId()+"-";
                ps.setInt(1, obj.getId());
                ps.setString(2, obj.getRegion());
                ps.setString(3, obj.getCountry());
                ps.setString(4, obj.getItemType());
                ps.setString(5, obj.getSalesChannel());
                ps.setString(6, obj.getPriority().toString());
                LocalDate localDate = LocalDate.parse(obj.getDate(), formatter);
                ps.setDate(7, Date.valueOf(localDate));
                LocalDate localShipDate = LocalDate.parse(obj.getShipDate(), formatter);
                ps.setDate(8, Date.valueOf(localShipDate));
                ps.setInt(9, obj.getUnitsSold());
                ps.setDouble(10, obj.getUnitPrice());
                ps.setDouble(11, obj.getUnitCost());
                ps.setDouble(12, obj.getTotalRevenue());
                ps.setDouble(13, obj.getTotalCost());
                ps.setDouble(14, obj.getTotalProfit());
                ps.addBatch();
            }
            ps.executeBatch();

        }
    }
}