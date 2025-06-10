package com.esPublico.kata.config;

import com.esPublico.kata.model.Order;
import com.esPublico.kata.service.ConsumerWorker;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Configura un pool de consumidores para procesar listas de objetos {@link Order}
 * en paralelo utilizando un {@link BlockingQueue}.
 * <p>
 * Esta clase se encarga de:
 * <ul>
 *     <li>Inicializar una cola compartida donde se depositan lotes de órdenes para procesar.</li>
 *     <li>Crear un {@link ExecutorService} con un número fijo de hilos configurado desde {@link ConfigLoader}.</li>
 *     <li>Lanzar múltiples instancias de {@link ConsumerWorker}, cada una ejecutándose en un hilo del pool.</li>
 * </ul>
 *
 * <p>Esta clase es <strong>thread-safe</strong> debido al uso de {@link LinkedBlockingQueue}
 * y al uso de un pool fijo de consumidores.</p>
 *
 */
public class ConsumerWorkerPoolConfig {

    /**
     * Cola compartida donde se insertan listas de objetos {@link Order} que serán consumidas
     * por los hilos del pool. Utiliza una implementación de {@link LinkedBlockingQueue} para
     * garantizar la seguridad en entornos multihilo.
     */
    public static final BlockingQueue<List<Order>> queue = new LinkedBlockingQueue<>();

    /**
     * Pool fijo de hilos encargado de ejecutar los consumidores. El número de hilos
     * se determina por el valor configurado en el fichero de propiedades mediante {@link ConfigLoader#getDdbbMaximumPoolSize()}.
     */
    public static final ExecutorService consumerPool;

    static {
        consumerPool = Executors.newFixedThreadPool(ConfigLoader.getDdbbMaximumPoolSize());
        // Lanzar consumidores
        for (int i = 0; i < ConfigLoader.getDdbbMaximumPoolSize(); i++) {
            consumerPool.submit(new ConsumerWorker(queue));
        }
    }
}
