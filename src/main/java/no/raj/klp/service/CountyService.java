package no.raj.klp.service;

import no.raj.klp.exception.CountyNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class CountyService {

    private static final Logger logger = LogManager.getLogger(CountyService.class);
    private final RestClient restClient;

    public CountyService(
            RestClient.Builder restClientBuilder,
            @Value("${county.service.base-url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public String getCountyName(String countyNumber) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/fylker/{fylkesnummer}", countyNumber)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || !response.containsKey("fylkesnavn")) {
                throw new CountyNotFoundException("County not found for number: " + countyNumber);
            }

            return (String) response.get("fylkesnavn");

        } catch (HttpClientErrorException.NotFound e) {
            throw new CountyNotFoundException("County not found for number: " + countyNumber);
        } catch (Exception e) {
            logger.error("Kartverket API error for countyNumber {}: {}", countyNumber, e.getMessage());
            throw e;
        }
    }
}
