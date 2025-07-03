package pe.edu.vallegrande.database.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.database.dto.AdmissionReasonDTO;
import pe.edu.vallegrande.database.service.AdmissionReasonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admission-reasons")
public class AdmissionReasonController {

    private final AdmissionReasonService admissionReasonService;

    public AdmissionReasonController(AdmissionReasonService admissionReasonService) {
        this.admissionReasonService = admissionReasonService;
    }

    /**
     * Obtiene todas las razones de admisión disponibles
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<AdmissionReasonDTO> getAllAdmissionReasons() {
        return admissionReasonService.findAll();
    }

    /**
     * Crea una nueva razón de admisión
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AdmissionReasonDTO> createAdmissionReason(@RequestBody AdmissionReasonDTO admissionReasonDTO) {
        return admissionReasonService.create(admissionReasonDTO);
    }
}