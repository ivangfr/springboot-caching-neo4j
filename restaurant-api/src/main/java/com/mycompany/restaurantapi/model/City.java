package com.mycompany.restaurantapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = "restaurants")
@ToString(exclude = "restaurants")
@Node
public class City {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Relationship(type = "LOCATED_IN", direction = Direction.INCOMING)
    private Set<Restaurant> restaurants = new LinkedHashSet<>();

}
