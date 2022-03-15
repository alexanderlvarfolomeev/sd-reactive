package ru.varfolomeev.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
public class Product extends DomainEntity {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("price")
    private final double rublePrice;
}
