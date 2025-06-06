package com.esPublico.kata.service;

import com.esPublico.kata.Main;
import com.esPublico.kata.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ConsumerWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);
    private final BlockingQueue<List<Order>> queue;

    public ConsumerWorker(BlockingQueue<List<Order>> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Order> lote = queue.take(); // espera si la cola está vacía
                if (lote.isEmpty()) break; // señal de fin
                //logger.debug("Insertando lote");
                DBService.getInstance().insertBatch(lote);
                //logger.debug("Lote insertado");
            }
        } catch (Exception e) {
            logger.error(e.getMessage()); // log real en producción
        }
    }
}
