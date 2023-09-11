package com.zerobase.stock.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dividend {

    private LocalDateTime date;
    private String dividend;

}
