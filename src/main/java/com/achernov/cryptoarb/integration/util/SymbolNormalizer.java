package com.achernov.cryptoarb.integration.util;

public class SymbolNormalizer {

  public static String normalize(String rawSymbol) {
    if (rawSymbol == null) return null;

    return rawSymbol.toUpperCase()
            .replaceAll("[/_\\- ]", "");
  }
}
