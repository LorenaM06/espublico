package com.esPublico.kata;

import com.esPublico.kata.model.Order;
import com.esPublico.kata.model.PageOrder;
import com.esPublico.kata.service.ApiService;
import com.esPublico.kata.service.ConsumerWorker;
import com.esPublico.kata.service.DBService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, SQLException {
        Long init1 = System.currentTimeMillis();

        /*
                PETICIONES API Y CARGA DE DATOS
         */
        ApiService apiService = new ApiService();
        int maxConsumers = 200;
        int queueSize = 50;
        BlockingQueue<List<Order>> queue = new LinkedBlockingQueue<>();
        DBService dbService = new DBService(miDataSource(maxConsumers));

        ExecutorService consumerPool = Executors.newFixedThreadPool(maxConsumers);
        // Lanzar consumidores
        for (int i = 0; i < maxConsumers; i++) {
            consumerPool.submit(new ConsumerWorker(queue, dbService));
        }

        // 900-1000
        PageOrder pageOrder = apiService.getOrders(String.valueOf(1), "1000");
        queue.put(pageOrder.getContent());
        String nextUri = pageOrder.getLinks().get("next");
        //logger.debug("Next uri: {} - array anterior: {}", nextUri, Integer.toHexString(System.identityHashCode(pageOrder.getContent())));
        boolean next = nextUri!=null;
        while(next){
            pageOrder = apiService.getOrders(nextUri);
            queue.put(pageOrder.getContent());
            nextUri = pageOrder.getLinks().get("next");
            //logger.debug("Next uri: {} - array anterior: {}", nextUri, Integer.toHexString(System.identityHashCode(pageOrder.getContent())));
            next = nextUri!=null;
        }
        Long finPeticionesApi = System.currentTimeMillis();

        for (int i = 0; i < maxConsumers; i++) {
            queue.put(Collections.emptyList()); // lotes vacíos como señal de fin
        }

        consumerPool.shutdown();
        consumerPool.awaitTermination(1, TimeUnit.HOURS);
        logger.debug("Carga de bbdd terminada");
        Long end1 = System.currentTimeMillis();


        /*
                RESUMEN DATOS
         */
        Long init2 = System.currentTimeMillis();
        dbService.executeGroupBy();
        Long end2 = System.currentTimeMillis();


        /*
                LOGS TOMAS DE TIEMPO
         */
        logger.debug("Tiempo peticiones API: {}", finPeticionesApi-init1);
        logger.debug("Tiempo extra carga bbdd: {}", end1-finPeticionesApi);
        logger.debug("Tiempo carga bbdd total: {}", end1-init1);
        logger.debug("Tiempo resumen total: {}", end2-init2);
        logger.debug("TIEMPO TOTAL: {}", end2-init1);
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