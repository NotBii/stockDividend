package com.zerobase.stock.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Company {

    private String ticker;


    private String name;

    public Company() {

    }
    public Company(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }

}
