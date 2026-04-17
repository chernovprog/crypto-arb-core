package com.achernov.cryptoarb.controller;

import com.achernov.cryptoarb.dto.metadata.AppMetadataDto;
import com.achernov.cryptoarb.dto.metadata.MetadataSection;
import com.achernov.cryptoarb.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class MetadataController {

  private final MetadataService metadataService;

  @GetMapping("/metadata")
  public AppMetadataDto getMetadata(@RequestParam(required = false) Set<MetadataSection> sections) {

    var finalSections = (sections == null || sections.isEmpty())
            ? EnumSet.allOf(MetadataSection.class)
            : sections;

    return metadataService.assembleMetadata(finalSections);
  }
}
