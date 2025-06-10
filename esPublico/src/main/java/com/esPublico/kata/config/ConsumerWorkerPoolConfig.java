package com.esPublico.kata.config;

import com.esPublico.kata.model.Order;
import com.esPublico.kata.service.ConsumerWorker;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerWorkerPoolConfig {

    public static final BlockingQueue<List<Order>> queue = new LinkedBlockingQueue<>();

    public static final ExecutorService consumerPool;

    static {
        consumerPool = Executors.newFixedThreadPool(ConfigLoader.getDdbbMaximumPoolSize());
        // Lanzar consumidores
        for (int i = 0; i < ConfigLoader.getDdbbMaximumPoolSize(); i++) {
            consumerPool.submit(new ConsumerWorker(queue));
        }
    }
}
