package com.esPublico.kata;

import com.esPublico.kata.model.PageOrder;
import com.esPublico.kata.service.ApiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        ApiService apiService = new ApiService();
        PageOrder pageOrder = apiService.getOrders(String.valueOf(995), "1000");
        logger.debug("registros recuperados: {}", pageOrder.getContent().size());
        String nextUri = pageOrder.getLinks().get("next");
        logger.debug("Next uri: {}", nextUri);
        boolean next = nextUri!=null;
        while(next){
            pageOrder = apiService.getOrders(nextUri);
            logger.debug("registros recuperados: {}", pageOrder.getContent().size());
            nextUri = pageOrder.getLinks().get("next");
            logger.debug("Next uri: {}", nextUri);
            next = nextUri!=null;
        }







        /*OrderService orderService = new OrderService();
        CsvExporter exporter = new CsvExporter();

        List<Order> orders = apiService.fetchOrders();
        orderService.saveAll(orders);
        orderService.printSummary();
        exporter.exportSortedOrders(orders);*/
    }
}