package net.pointofviews.common.toss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class TossConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    public TossClient tossClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(TossClient.class);
    }
}
