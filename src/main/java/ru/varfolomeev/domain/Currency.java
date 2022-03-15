package ru.varfolomeev.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Currency {
    RUB(1.0), USD(0.01), EUR(0.0095);

    private final double rublesMultiplier;

    public double fromRubles(double rubles) {
        return rubles * rublesMultiplier;
    }

    public double toRubles(double currency) {
        return currency / rublesMultiplier;
    }
}
