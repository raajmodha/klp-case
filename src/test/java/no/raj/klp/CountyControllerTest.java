package no.raj.klp;

import no.raj.klp.service.CountyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.raj.klp.exception.CountyNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
class CountyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountyService countyService;

    @TestConfiguration
    static class MockCountyServiceConfig {
        @Bean
        @Primary
        CountyService countyService() {
            return mock(CountyService.class);
        }
    }

    @Test
    void returnsCountyNameAsPlainText() throws Exception {
        when(countyService.getCountyName("03")).thenReturn("Oslo");

        mockMvc.perform(get("/county/03"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Oslo"));
    }

    @Test
    void returns400WhenCountyNumberIsNonNumeric() throws Exception {
        mockMvc.perform(get("/county/ab"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400WhenCountyNumberIsTooShort() throws Exception {
        mockMvc.perform(get("/county/3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns400WhenCountyNumberIsTooLong() throws Exception {
        mockMvc.perform(get("/county/034"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns404WhenCountyNumberNotFound() throws Exception {
        when(countyService.getCountyName("99")).thenThrow(new CountyNotFoundException("County not found for number: 99"));

        mockMvc.perform(get("/county/99"))
                .andExpect(status().isNotFound());
    }
}
