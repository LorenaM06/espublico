package com.esPublico.kata.service;

import com.esPublico.kata.Main;
import com.esPublico.kata.config.ConsumerWorkerPoolConfig;
import com.esPublico.kata.config.DataSourceConfig;
import com.esPublico.kata.model.Order;
import com.esPublico.kata.model.PageOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class DDBBDataLoader {

    private static final DDBBDataLoader INSTANCE = new DDBBDataLoader();
    public static DDBBDataLoader getInstance(){
        return INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(DDBBDataLoader.class);

    public Long loadDDBB() throws SQLException, IOException, URISyntaxException, InterruptedException {
        Long init1 = System.currentTimeMillis();


        String maxPerPage = "1000";
        int totalOrders = Integer.valueOf(maxPerPage);
        String nextUri = firstRequest(ConsumerWorkerPoolConfig.queue, maxPerPage);
        totalOrders += nextRequest(nextUri, ConsumerWorkerPoolConfig.queue);
        endRequest();
        Long finPeticionesApi = System.currentTimeMillis();

        logger.debug("Carga de bbdd terminada");
        Long end1 = System.currentTimeMillis();
        logger.debug("Tiempo peticiones API: {} - Total registros recuperados: {}", finPeticionesApi-init1, totalOrders);
        logger.debug("Tiempo extra carga bbdd: {}", end1-finPeticionesApi);
        return end1-init1;
    }

    private String firstRequest(BlockingQueue<List<Order>> queue, String maxPerPage) throws IOException, URISyntaxException, InterruptedException {
        PageOrder pageOrder = ApiService.getInstance().getOrders(String.valueOf(1), maxPerPage);
        queue.put(pageOrder.getContent());
        return pageOrder.getLinks().get("next");
    }

    private int nextRequest(String nextUri, BlockingQueue<List<Order>> queue) throws IOException, InterruptedException {
        int total = 0;
        while(nextUri!=null){
            PageOrder pageOrder = ApiService.getInstance().getOrders(nextUri);
            queue.put(pageOrder.getContent());
            nextUri = pageOrder.getLinks().get("next");
            total += pageOrder.getContent().size();
        }
        return total;
    }

    private void endRequest() throws InterruptedException {
        int consumerPoolSize = ((ThreadPoolExecutor) ConsumerWorkerPoolConfig.consumerPool).getPoolSize();

        for (int i = 0; i < consumerPoolSize; i++) {
            ConsumerWorkerPoolConfig.queue.put(Collections.emptyList()); // lotes vacíos como señal de fin
        }

        ConsumerWorkerPoolConfig.consumerPool.shutdown();
        ConsumerWorkerPoolConfig.consumerPool.awaitTermination(1, TimeUnit.HOURS);
    }
}
