package com.ivanfranchin.restaurantapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "city")
@ToString(exclude = "city")
@Node
public class Restaurant {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @JsonIgnore
    @Relationship(type = "LOCATED_IN")
    private City city;

    @JsonIgnore
    @Relationship(type = "HAS")
    private Set<Dish> dishes = new LinkedHashSet<>();

    public Restaurant(String name, City city) {
        this.name = name;
        this.city = city;
    }
}
