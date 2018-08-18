package com.mycompany.springbootneo4jcaffeine.model;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.id.UuidStrategy;

@Data
@NodeEntity
public class City {

    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    private String id;

    private String name;

}
