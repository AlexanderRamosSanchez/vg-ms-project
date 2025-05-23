package pe.edu.vallegrande.information.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.information.model.BasicService;
import pe.edu.vallegrande.information.service.BasicServiceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/services")
public class BasicServiceController {
    
    private final BasicServiceService basicServiceService;
    
    @Autowired
    public BasicServiceController(BasicServiceService basicServiceService) {
        this.basicServiceService = basicServiceService;
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<BasicService> getAllServices() {
        return basicServiceService.findAll();
    }
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BasicService>> getServiceById(@PathVariable Integer id) {
        return basicServiceService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BasicService>> createService(@RequestBody BasicService basicService) {
        return basicServiceService.save(basicService)
                .map(savedService -> ResponseEntity.status(HttpStatus.CREATED).body(savedService));
    }
    
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BasicService>> updateService(@PathVariable Integer id, @RequestBody BasicService basicService) {
        return basicServiceService.update(id, basicService)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> deleteService(@PathVariable Integer id) {
        return basicServiceService.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
