package com.esPublico.kata.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class PageOrder implements Serializable {

    private String uuid;
    private int page;
    private ArrayList<Order> content;
    private HashMap<String, String> links;
}
