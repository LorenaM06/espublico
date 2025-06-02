package com.esPublico.kata;

import com.esPublico.kata.model.Order;
import com.esPublico.kata.model.PageOrder;
import com.esPublico.kata.service.ApiService;
import com.esPublico.kata.service.ConsumerWorker;
import com.esPublico.kata.service.DBService;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, SQLException {
        Long init = System.currentTimeMillis();
        ApiService apiService = new ApiService();

        /*
        Con 30 -> 38522ms
        con 50 -> 26733ms CPU 33% Mem 39%
        con 100 -> 21223 CPU 41% Men 40%
        con 500 -> 19635 CPU 48% Mem 40%
         */
        int maxConsumers = 700;
        int queueSize = 50;
        BlockingQueue<List<Order>> queue = new ArrayBlockingQueue<>(queueSize);
        DBService inserter = new DBService(miDataSource(maxConsumers));

        ExecutorService consumerPool = Executors.newFixedThreadPool(maxConsumers);
        // Lanzar consumidores
        for (int i = 0; i < maxConsumers; i++) {
            consumerPool.submit(new ConsumerWorker(queue, inserter));
        }

        PageOrder pageOrder = apiService.getOrders(String.valueOf(900), "1000");
        logger.debug("registros recuperados: {}", pageOrder.getContent().size());
        /*inserter.insertBatch(pageOrder.getContent());*/
        queue.put(pageOrder.getContent());
        String nextUri = pageOrder.getLinks().get("next");
        logger.debug("Next uri: {}", nextUri);
        boolean next = nextUri!=null;
        while(next){
            pageOrder = apiService.getOrders(nextUri);
            logger.debug("registros recuperados: {}", pageOrder.getContent().size());
            /*inserter.insertBatch(pageOrder.getContent());*/
            queue.put(pageOrder.getContent());
            nextUri = pageOrder.getLinks().get("next");
            logger.debug("Next uri: {}", nextUri);
            next = nextUri!=null;
        }

        for (int i = 0; i < maxConsumers; i++) {
            queue.put(Collections.emptyList()); // lotes vacíos como señal de fin
        }

        consumerPool.shutdown();
        consumerPool.awaitTermination(1, TimeUnit.HOURS);
        logger.debug("FIN!!!");
        Long end = System.currentTimeMillis();
        logger.debug("Tiempo total: {}", end-init);







        /*OrderService orderService = new OrderService();
        CsvExporter exporter = new CsvExporter();

        List<Order> orders = apiService.fetchOrders();
        orderService.saveAll(orders);
        orderService.printSummary();
        exporter.exportSortedOrders(orders);*/
    }

    public static HikariDataSource miDataSource(int pool) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/espublico");
        config.setUsername("root");
        config.setPassword("root");

        // Opcionales pero recomendados
        config.setMaximumPoolSize(pool); // Número máximo de conexiones simultáneas
        config.setMinimumIdle(pool);      // Conexiones mínimas en reposo
        config.setIdleTimeout(30000);  // 30 segundos
        config.setMaxLifetime(600000); // 10 minutos
        config.setConnectionTimeout(10000); // 10 segundos de espera máxima

        return new HikariDataSource(config);
    }
}