package com.esPublico.kata.service;

import com.esPublico.kata.Main;
import com.esPublico.kata.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DBService {
    private static final Logger logger = LoggerFactory.getLogger(DBService.class);
    private static final String sqlInsert = "INSERT INTO orders ( id, region, country, item_type, sales_channel, priority, order_date, ship_date, units_sold, unit_price, unit_cost, total_revenue, total_cost, total_profit, uuid ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final String[] fields = {"region", "country", "item_type", "sales_channel", "priority"};
    private final DataSource dataSource;


    public DBService(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
    }

    public void insertBatch(List<Order> lista) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            for (Order obj : lista) {
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
                ps.setString(15, obj.getUuid());
                ps.addBatch();
            }
            int[] registrosInsertados = ps.executeBatch();
        } catch (SQLException e){
            logger.error(e.getMessage());
        }
    }

    public void executeGroupBy() {
        for (String field: fields) {
            String query = "SELECT " + field + ", COUNT(*) AS total FROM orders GROUP BY " + field + " ORDER BY " + field;

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("----- " + field.toUpperCase() + " -----");
                while (rs.next()) {
                    System.out.println(rs.getString(1) + ": " + rs.getInt("total"));
                }

            } catch (SQLException e) {
                System.err.println("Error processing field " + field + ": " + e.getMessage());
            }
        }
    }
}