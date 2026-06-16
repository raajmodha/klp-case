package no.raj.klp.controller;

import jakarta.validation.constraints.Pattern;
import no.raj.klp.service.CountyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Validated
@RestController
@RequestMapping("/county")
public class CountyController {

    private static final Logger logger = LogManager.getLogger(CountyController.class);
    private final CountyService countyService;

    public CountyController(CountyService countyService) {
        this.countyService = countyService;
    }

    @GetMapping(value = "/{countyNumber}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getCountyName(
            @Pattern(regexp = "\\d{2}", message = "County number must be exactly 2 digits")
            @PathVariable String countyNumber) {
        return ResponseEntity.ok(countyService.getCountyName(countyNumber));
    }
}
