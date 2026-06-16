package no.raj.klp.service;

import no.raj.klp.exception.CountyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class CountyServiceTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private RestClient.RequestHeadersSpec headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private CountyService countyService;

    @BeforeEach
    void setUp() {
        when(restClientBuilder.baseUrl(any(String.class))).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);
        countyService = new CountyService(restClientBuilder, "https://ws.geonorge.no/kommuneinfo/v1");
    }

    @Test
    void returnsCountyNameForValidCountyNumber() {
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(String.class), any(Object.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(Map.of("fylkesnavn", "Oslo"));

        String result = countyService.getCountyName("03");

        assertThat(result).isEqualTo("Oslo");
    }

    @Test
    void throwsCountyNotFoundExceptionWhenApiReturns404() {
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(String.class), any(Object.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> countyService.getCountyName("99"))
                .isInstanceOf(CountyNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void throwsCountyNotFoundExceptionWhenResponseMissesFylkesnavn() {
        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(String.class), any(Object.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(Map.of("someOtherField", "value"));

        assertThatThrownBy(() -> countyService.getCountyName("03"))
                .isInstanceOf(CountyNotFoundException.class);
    }
}
