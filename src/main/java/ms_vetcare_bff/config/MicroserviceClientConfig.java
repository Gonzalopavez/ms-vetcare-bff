package ms_vetcare_bff.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.client.RestClient;

@Configuration
public class MicroserviceClientConfig {

    @Bean(name = "consultationsRestClient")
    public RestClient consultationsRestClient(
            @Value("${vetcare.services.consultations.base-url}")
            String baseUrl
    ) {

        return RestClient
            .builder()
            .baseUrl(baseUrl)
            .build();
    }


    @Bean(name = "catalogRestClient")
    public RestClient catalogRestClient(
            @Value("${vetcare.services.catalog.base-url}")
            String baseUrl
    ) {

        return RestClient
            .builder()
            .baseUrl(baseUrl)
            .build();
    }
}