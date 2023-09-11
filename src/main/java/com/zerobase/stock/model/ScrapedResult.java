package com.zerobase.stock.model;

import lombok.*;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedResult {

    private Company company;

    private List<Dividend> dividends;


}
