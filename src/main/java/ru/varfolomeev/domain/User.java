package ru.varfolomeev.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
public class User extends DomainEntity {
    @JsonProperty("login")
    private final String login;

    @JsonProperty("password")
    private final String password;

    @JsonProperty("currency")
    private final Currency currency;
}
