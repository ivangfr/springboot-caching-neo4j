package com.ivanfranchin.restaurantapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class RestResponsePageImpl<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePageImpl(@JsonProperty("content") List<T> content,
                                @JsonProperty("number") Integer number,
                                @JsonProperty("size") Integer size,
                                @JsonProperty("totalElements") Long totalElements) {
        super(
                content,
                PageRequest.of(number == null ? 0 : number, size == null ? 1 : size),
                totalElements == null ? 0 : totalElements
        );
    }
}
