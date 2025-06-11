package com.esPublico.kata.service;

import com.esPublico.kata.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

public class ConsumerWorkerTest {

    private DBService dbServiceMock;
    private BlockingQueue<List<Order>> queue;

    @BeforeEach
    void setUp() {
        dbServiceMock = mock(DBService.class);
        queue = new ArrayBlockingQueue<>(10);
    }

    @Test
    void testConsumeAndInsertBatch() throws InterruptedException {
        List<Order> lote = List.of(new Order());
        List<Order> lote2 = List.of(new Order());
        queue.put(lote);
        queue.put(lote2);
        queue.put(List.of());

        Thread consumerThread = new Thread(new ConsumerWorker(queue, dbServiceMock));
        consumerThread.start();
        consumerThread.join();

        verify(dbServiceMock, times(1)).insertBatch(lote);
        verify(dbServiceMock, times(1)).insertBatch(lote2);
    }

    @Test
    void testEmptyLoteStopsConsumer() throws InterruptedException {
        queue.put(List.of());

        Thread consumerThread = new Thread(new ConsumerWorker(queue, dbServiceMock));
        consumerThread.start();
        consumerThread.join();

        verifyNoInteractions(dbServiceMock);
    }

    @Test
    void testExceptionIsHandled() throws Exception {
        List<Order> lote = List.of(new Order());
        List<Order> lote2 = List.of(new Order());
        queue.put(lote);
        queue.put(lote2);
        queue.put(List.of());

        doThrow(new RuntimeException("fallo insertando")).when(dbServiceMock).insertBatch(lote);

        Thread consumerThread = new Thread(new ConsumerWorker(queue, dbServiceMock));
        consumerThread.start();
        consumerThread.join();
        // Se comprueba que aunque falle un lote el hilo no termina y continua procesando más lotes
        verify(dbServiceMock, times(1)).insertBatch(lote);
        verify(dbServiceMock, times(1)).insertBatch(lote2);
    }
}
