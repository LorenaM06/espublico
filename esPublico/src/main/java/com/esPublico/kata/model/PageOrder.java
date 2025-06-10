package com.esPublico.kata.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa una página de resultados que contiene una lista de pedidos (Order).
 * Implementa Serializable para permitir la serialización del objeto.
 *
 * Contiene información sobre la página actual, el contenido (lista de pedidos)
 * y un conjunto de enlaces relacionados, que pueden ser usados para navegación o referencia.
 *
 */
@Getter
@Setter
public class PageOrder implements Serializable {

    /**
     * Número de la página actual.
     */
    private int page;

    /**
     * Lista de pedidos contenidos en esta página.
     */
    private ArrayList<Order> content;

    /**
     * Mapa de enlaces relacionados con esta página (por ejemplo, enlaces de navegación).
     */
    private HashMap<String, String> links;
}
