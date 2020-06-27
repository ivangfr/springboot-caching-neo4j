package com.mycompany.restaurantapi.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.id.UuidStrategy;

import java.math.BigDecimal;

@Data
@NodeEntity
public class Dish {

    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    private String id;

    private String name;

    private BigDecimal price;

}
