package com.achernov.cryptoarb.dto.metadata;

public enum MetadataSection {
  EXCHANGE_SUBSCRIPTIONS("exchange-subscriptions"),
  TRADING_PAIR_SUBSCRIPTIONS("trading-pair-subscriptions"),
  CURRENCY_SUBSCRIPTIONS("currency-subscriptions"),
  UI_CONFIG("ui-config");

  private final String value;

  MetadataSection(String value) {
    this.value = value;
  }

  public static MetadataSection fromValue(String text) {
    for (MetadataSection section : MetadataSection.values()) {
      if (section.value.equalsIgnoreCase(text)) {
        return section;
      }
    }

    throw new IllegalArgumentException("Unknown metadata section: " + text);
  }
}
