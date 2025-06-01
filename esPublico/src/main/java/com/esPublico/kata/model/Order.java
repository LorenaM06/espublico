package com.esPublico.kata.model;

import com.esPublico.kata.util.PriorityEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

@Getter
@Setter
public class Order implements Serializable {

    private String uuid;
    private int id;
    private String region;
    private String country;
    private String itemType;
    private String salesChannel;
    private PriorityEnum priority;
    private String date;
    private String shipDate;
    private int unitsSold;
    private double unitPrice;
    private double unitCost;
    private double totalRevenue;
    private double totalCost;
    private double totalProfit;
    private HashMap<String, String> links;
}
