package com.esPublico.kata.service;

import com.esPublico.kata.config.ConfigLoader;
import com.esPublico.kata.config.ConsumerWorkerPoolConfig;
import com.esPublico.kata.model.PageOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.*;

/**
 * Clase encargada de orquestar la carga de datos en la base de datos desde una API externa.
 *
 * <p>Esta clase está implementada como un singleton, por lo que se debe acceder a su instancia
 * a través de {@link #getInstance()}.
 *
 * <p>Requiere configuración previa en {@link ConsumerWorkerPoolConfig} para establecer el pool de hilos
 * y la cola compartida entre productores y consumidores.
 */
public class DDBBDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DDBBDataLoader.class);

    private static final DDBBDataLoader INSTANCE = new DDBBDataLoader();
    /**
     * Devuelve la instancia única de {@code DDBBDataLoader}.
     *
     * @return la instancia singleton.
     */
    public static DDBBDataLoader getInstance(){
        return INSTANCE;
    }

    /**
     * Inicia el proceso de carga de datos en la base de datos desde la API externa.
     *
     * <p>La carga se realiza en varios pasos:
     *  <ul>
     *      <li>Petición inicial a la API para obtener la primera página de órdenes.</li>
     *      <li>Petición secuencial a la API para recuperar las siguientes páginas utilizando el enlace {@code next}.</li>
     *      <li>Inserción de los datos en la base de datos mediante un pool de consumidores en paralelo.</li>
     *      <li>Finalización del procesamiento y cierre controlado del pool de hilos.</li>
     *  </ul>
     *
     * @return el tiempo total de ejecución en milisegundos.
     * @throws IOException si ocurre un error en la comunicación con la API.
     * @throws URISyntaxException si la URI de la API no es válida.
     * @throws InterruptedException si algún hilo es interrumpido durante la ejecución.
     */
    public Long loadDDBB() throws IOException, URISyntaxException, InterruptedException {
        Long init1 = System.currentTimeMillis();


        String maxPerPage = ConfigLoader.getApiMaxPerPage();
        int totalOrders = Integer.parseInt(maxPerPage);
        String nextUri = firstRequest(maxPerPage);
        totalOrders += nextRequest(nextUri);
        endRequest();
        Long finPeticionesApi = System.currentTimeMillis();

        logger.debug("Carga de bbdd terminada");
        Long end1 = System.currentTimeMillis();
        logger.debug("Tiempo peticiones API: {} - Total registros recuperados: {}", finPeticionesApi-init1, totalOrders);
        logger.debug("Tiempo extra carga bbdd: {}", end1-finPeticionesApi);
        return end1-init1;
    }

    /**
     * Realiza la primera petición a la API para obtener la primera página de órdenes
     * y coloca el lote de órdenes en la cola compartida para ser procesado por los consumidores.
     *
     * @param maxPerPage el número máximo de elementos por página definido en la configuración.
     * @return la URI del siguiente lote de datos (enlace {@code next}) o {@code null} si no hay más datos.
     * @throws IOException si ocurre un error de comunicación con la API.
     * @throws URISyntaxException si la URI de la API no es válida.
     * @throws InterruptedException si la inserción en la cola es interrumpida.
     */
    private String firstRequest(String maxPerPage) throws IOException, URISyntaxException, InterruptedException {
        PageOrder pageOrder = ApiService.getInstance().getOrders(String.valueOf(1), maxPerPage);
        ConsumerWorkerPoolConfig.queue.put(pageOrder.getContent());
        return pageOrder.getLinks().get("next");
    }

    /**
     * Realiza peticiones sucesivas a la API utilizando el enlace {@code next},
     * recupera las páginas de órdenes restantes y las pone en la cola para ser procesadas.
     *
     * @param nextUri la URI de la siguiente página de datos.
     * @return el número total de órdenes recuperadas en esta fase.
     * @throws IOException si ocurre un error de comunicación con la API.
     * @throws InterruptedException si la inserción en la cola es interrumpida.
     */
    private int nextRequest(String nextUri) throws IOException, InterruptedException {
        int total = 0;
        while(nextUri!=null){
            PageOrder pageOrder = ApiService.getInstance().getOrders(nextUri);
            ConsumerWorkerPoolConfig.queue.put(pageOrder.getContent());
            nextUri = pageOrder.getLinks().get("next");
            total += pageOrder.getContent().size();
        }
        return total;
    }

    /**
     * Finaliza el proceso de carga de datos enviando señales de terminación
     * a los consumidores (en forma de listas vacías) y cerrando el pool de hilos.
     *
     * <p>Espera hasta una hora a que todos los consumidores finalicen su ejecución. Si no han terminado los
     * interrumpe y muestra un mensaje de error.
     *
     * @throws InterruptedException si el hilo principal es interrumpido mientras espera.
     */
    private void endRequest() throws InterruptedException {
        int consumerPoolSize = ((ThreadPoolExecutor) ConsumerWorkerPoolConfig.consumerPool).getPoolSize();

        for (int i = 0; i < consumerPoolSize; i++) {
            ConsumerWorkerPoolConfig.queue.put(Collections.emptyList()); // lotes vacíos como señal de fin
        }

        ConsumerWorkerPoolConfig.consumerPool.shutdown();
        if (!ConsumerWorkerPoolConfig.consumerPool.awaitTermination(1, TimeUnit.HOURS)){
            logger.error("No se han insertado todos los lotes recuperados de la API después de una hora.");
        }
    }
}
