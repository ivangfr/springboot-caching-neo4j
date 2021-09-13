package com.mycompany.restaurantapi.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Node
public class Dish {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private BigDecimal price;
}
