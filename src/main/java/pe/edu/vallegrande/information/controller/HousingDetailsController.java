package pe.edu.vallegrande.information.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.information.model.HousingDetails;
import pe.edu.vallegrande.information.service.HousingDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/housing")
public class HousingDetailsController {
    
    private final HousingDetailsService housingDetailsService;
    
    @Autowired
    public HousingDetailsController(HousingDetailsService housingDetailsService) {
        this.housingDetailsService = housingDetailsService;
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<HousingDetails> getAllHousingDetails() {
        return housingDetailsService.findAll();
    }
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HousingDetails>> getHousingDetailsById(@PathVariable Integer id) {
        return housingDetailsService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HousingDetails>> createHousingDetails(@RequestBody HousingDetails housingDetails) {
        return housingDetailsService.save(housingDetails)
                .map(savedHousing -> ResponseEntity.status(HttpStatus.CREATED).body(savedHousing));
    }
    
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<HousingDetails>> updateHousingDetails(@PathVariable Integer id, @RequestBody HousingDetails housingDetails) {
        return housingDetailsService.update(id, housingDetails)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> deleteHousingDetails(@PathVariable Integer id) {
        return housingDetailsService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
