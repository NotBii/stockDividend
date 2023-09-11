package com.zerobase.stock.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Company {

    private String ticker;


    private String name;


}
