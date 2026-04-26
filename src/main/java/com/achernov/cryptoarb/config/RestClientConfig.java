package com.achernov.cryptoarb.config;

import com.achernov.cryptoarb.config.properties.CoinMarketCapProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@AllArgsConstructor
public class RestClientConfig {

  private final CoinMarketCapProperties props;

  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  @Bean
  public RestClient coinMarketCapClient(RestClient.Builder builder) {
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
    factory.setReadTimeout(props.api().timeout().read());

    return builder
            .baseUrl(props.baseUrl())
            .requestFactory(factory)
            .defaultHeader(props.headerName(), props.apiKey())
            .build();
  }
}
