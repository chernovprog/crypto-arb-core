package com.achernov.cryptoarb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Currency {

  private Long id;
  private String name;
  private String ticker;
}
