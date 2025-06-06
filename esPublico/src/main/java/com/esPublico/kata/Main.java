package com.esPublico.kata;

import com.esPublico.kata.service.DBService;
import com.esPublico.kata.service.DDBBDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, SQLException, ExecutionException {
        // Consultar a la API y cargar los datos en bbdd
        Long dataLoaderTime =  DDBBDataLoader.getInstance().loadDDBB();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Resumen del número de pedidos de cada tipo según distintas columnas
        Callable<Long> summaryTask = () -> {
            return DBService.getInstance().executeGroupBy();
        };

        // Generar un fichero con los registros ordenados por número de pedido
        Callable<Long> createCsvTask = () -> {
            return DBService.getInstance().selectAll();
        };


        Future<Long> futureCreateCsv = executor.submit(createCsvTask);
        Future<Long> futureSummary = executor.submit(summaryTask);

        Long summaryTime = futureSummary.get();
        Long createCsvTime = futureCreateCsv.get();
        executor.shutdown();

        // Tiempos
        logger.debug("Tiempo carga bbdd total: {}", dataLoaderTime);
        logger.debug("Tiempo generación resumen: {}", summaryTime);
        logger.debug("Tiempo generación csv: {}", createCsvTime);
        logger.debug("TIEMPO TOTAL: {}", dataLoaderTime + summaryTime + createCsvTime);
    }
}