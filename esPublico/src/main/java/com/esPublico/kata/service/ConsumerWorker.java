package com.esPublico.kata.service;

import com.esPublico.kata.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * {@code ConsumerWorker} es una clase que implementa la interfaz {@link Runnable}
 * y actúa como consumidor en un patrón productor-consumidor.
 *
 * <p>Consume lotes de objetos {@link Order} desde una {@link BlockingQueue} y los
 * inserta en la base de datos mediante el servicio {@link DBService}.
 * </p>
 *
 * <p>El consumo continúa hasta que recibe un lote vacío, el cual se interpreta como
 * una señal de finalización.</p>
 *
 * <p>Los errores se muestran por log mediante SLF4J.</p>
 *
 */
public class ConsumerWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);
    private final BlockingQueue<List<Order>> queue;
    private final DBService dbService;

    /**
     * Crea una nueva instancia de {@code ConsumerWorker}.
     *
     * @param queue      la cola compartida desde la que se obtendrán los lotes de órdenes.
     *                   No debe ser {@code null}.
     * @param dbService  el servicio de base de datos que se utilizará para insertar los lotes.
     *                   No debe ser {@code null}.
     */
    public ConsumerWorker(BlockingQueue<List<Order>> queue, DBService dbService) {
        this.queue = queue;
        this.dbService = dbService;
    }

    /**
     * Ejecuta el proceso consumidor que extrae lotes de órdenes de la cola y los inserta en la base de datos.
     * <p>
     * El método se ejecuta en un hilo separado y realiza las siguientes acciones en un bucle infinito:
     * <ul>
     *     <li>Extrae un lote de la cola (bloqueante si la cola está vacía).</li>
     *     <li>Si el lote está vacío, considera que es la señal para finalizar la ejecución y termina el bucle.</li>
     *     <li>Intenta insertar el lote en la base de datos mediante {@code dbService.insertBatch(lote)}.</li>
     *     <li>Si ocurre cualquier excepción durante la inserción, la captura y registra el error, pero continúa procesando lotes posteriores.</li>
     * </ul>
     *
     * Este diseño permite que el consumidor maneje errores puntuales sin detener completamente el procesamiento.
     */
    @Override
    public void run() {
        while (true) {
            try {
                List<Order> lote = queue.take(); // espera si la cola está vacía
                if (lote.isEmpty()) break; // señal de fin
                dbService.insertBatch(lote);
            } catch (Exception e) {
                logger.error("Error insertando lote en bbdd: {}", e.getMessage());
            }
        }
    }
}
