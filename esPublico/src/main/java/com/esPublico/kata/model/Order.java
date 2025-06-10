package com.esPublico.kata.model;

import com.esPublico.kata.util.PriorityEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Representa un pedido (Order) con sus atributos principales.
 * Implementa Serializable para permitir la serialización del objeto.
 *
 * Cada pedido contiene información como identificadores, datos geográficos,
 * detalles del producto, fechas relevantes, cantidades, precios y beneficios.
 * También incluye un mapa de enlaces relacionados.
 *
 * @author [Tu Nombre]
 * @version 1.0
 */
@Getter
@Setter
public class Order implements Serializable {

    /**
     * Identificador único del pedido.
     */
    private String uuid;

    /**
     * Identificador numérico del pedido.
     */
    private int id;

    /**
     * Región geográfica donde se realizó el pedido.
     */
    private String region;

    /**
     * País donde se realizó el pedido.
     */
    private String country;

    /**
     * Tipo de producto del pedido.
     */
    private String itemType;

    /**
     * Canal de ventas usado.
     */
    private String salesChannel;

    /**
     * Prioridad del pedido.
     */
    private PriorityEnum priority;

    /**
     * Fecha en que se realizó el pedido en formado M/d/yyyy.
     */
    private String date;

    /**
     * Fecha estimada o real de envío del pedido.
     */
    private String shipDate;

    /**
     * Número de unidades vendidas.
     */
    private int unitsSold;

    /**
     * Precio unitario del producto.
     */
    private double unitPrice;

    /**
     * Coste unitario del producto.
     */
    private double unitCost;

    /**
     * Ingresos totales generados por el pedido.
     */
    private double totalRevenue;

    /**
     * Costes totales asociados al pedido.
     */
    private double totalCost;

    /**
     * Beneficio total del pedido.
     */
    private double totalProfit;

    /**
     * Mapa de enlaces relacionados con el pedido.
     */
    private HashMap<String, String> links;
}
