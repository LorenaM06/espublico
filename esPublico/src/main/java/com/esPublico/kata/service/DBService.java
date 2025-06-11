package com.esPublico.kata.service;

import com.esPublico.kata.config.DataSourceConfig;
import com.esPublico.kata.model.Order;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio singleton para operaciones de base de datos.
 * <p>
 * Utiliza una conexión configurada a través de {@link DataSourceConfig}.
 * </p>
 */
public class DBService {

    private static final DBService INSTANCE = new DBService();

    /**
     * Obtiene la instancia singleton de {@code DBService}.
     *
     * @return instancia única de {@code DBService}.
     */
    public static DBService getInstance(){
        return INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(DBService.class);
    private static final String sqlInsert = "INSERT INTO orders ( id, region, country, item_type, sales_channel, priority, order_date, ship_date, units_sold, unit_price, unit_cost, total_revenue, total_cost, total_profit, uuid ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String sqlSelect = "select id, uuid, region, country, item_type, sales_channel, priority, order_date, ship_date, units_sold, unit_price, unit_cost, total_revenue, total_cost, total_profit from orders order by id;";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final String[] fields = {"region", "country", "item_type", "sales_channel", "priority"};

    /**
     * Inserta un lote de órdenes en la base de datos utilizando ejecución batch.
     * <p>
     * Cada objeto {@link Order} del listado es mapeado a los parámetros del
     * {@code PreparedStatement} para la inserción.
     * </p>
     *
     * @param lista lista de órdenes a insertar; no debe ser {@code null}.
     */
    public void insertBatch(List<Order> lista) {
        try (Connection conn = DataSourceConfig.ds.getConnection();
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

    /**
     * Ejecuta consultas de agrupamiento {@code GROUP BY} para varios campos predefinidos,
     * mostrando el conteo total de órdenes para cada valor de campo.
     * <p>
     * Los campos considerados son: {@code region}, {@code country}, {@code item_type}, {@code sales_channel} y {@code priority}.
     * </p>
     *
     * @return tiempo en milisegundos que tardó en ejecutar todas las consultas.
     */
    public Long executeAndLogGroupBy() {
        long init = System.currentTimeMillis();
        for (String field: fields) {
            String query = "SELECT " + field + ", COUNT(*) AS total FROM orders GROUP BY " + field + " ORDER BY " + field;

            try (Connection conn = DataSourceConfig.ds.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("----- " + field.toUpperCase() + " -----");
                while (rs.next()) {
                    System.out.println(rs.getString(1) + ": " + rs.getInt("total"));
                }

            } catch (SQLException e) {
                System.err.println("Error processing field " + field + ": " + e.getMessage());
            }
        }
        return System.currentTimeMillis() - init;
    }

    /**
     * Ejecuta consultas de agrupamiento {@code GROUP BY} para varios campos predefinidos,
     * mostrando el conteo total de órdenes para cada valor de campo.
     * <p>
     * Los campos considerados son: {@code region}, {@code country}, {@code item_type}, {@code sales_channel} y {@code priority}.
     * </p>
     *
     * @return tiempo en milisegundos que tardó en ejecutar todas las consultas.
     */
    public Long exportOrdersToCsv() throws SQLException, IOException {
        long init = System.currentTimeMillis();
        try (Connection conn = DataSourceConfig.ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSelect);
             ResultSet rs = ps.executeQuery()) {

            Writer writer = new FileWriter("orders.csv");
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.getFormat().setDelimiter(';');
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Escribir cabecera con nombres de columnas
            String[] header = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                header[i - 1] = meta.getColumnName(i);
            }
            csvWriter.writeRow((Object[]) header);
            // Escribir filas con los datos
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                csvWriter.writeRow((Object[]) row);
            }
            csvWriter.close();

            return System.currentTimeMillis()-init;
        }
    }
}