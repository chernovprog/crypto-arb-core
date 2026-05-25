package com.achernov.cryptoarb.config.cache;

import com.achernov.cryptoarb.dto.metadata.MetadataSection;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("cacheKeyHelper")
public class CacheKeyHelper {

  public String getMetadataKey(Set<MetadataSection> sections) {
    if (sections == null || sections.isEmpty()) {
      return "all";
    }
    return sections.stream()
            .sorted()
            .map(Enum::name)
            .collect(Collectors.joining("-"));
  }
}
