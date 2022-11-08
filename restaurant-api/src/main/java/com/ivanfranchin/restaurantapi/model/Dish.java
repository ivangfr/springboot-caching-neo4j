package com.ivanfranchin.restaurantapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Node
public class Dish {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private BigDecimal price;

    public Dish(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}
