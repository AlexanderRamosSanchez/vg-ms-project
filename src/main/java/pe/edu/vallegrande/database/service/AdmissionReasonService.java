package pe.edu.vallegrande.database.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.database.dto.AdmissionReasonDTO;
import pe.edu.vallegrande.database.model.AdmissionReason;
import pe.edu.vallegrande.database.repository.AdmissionReasonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdmissionReasonService {

    private final AdmissionReasonRepository admissionReasonRepository;

    @Autowired
    public AdmissionReasonService(AdmissionReasonRepository admissionReasonRepository) {
        this.admissionReasonRepository = admissionReasonRepository;
    }

    /**
     * Obtiene todas las razones de admisión
     */
    public Flux<AdmissionReasonDTO> findAll() {
        return admissionReasonRepository.findAll()
                .map(this::mapToDTO);
    }

    /**
     * Obtiene una razón de admisión por ID
     */
    public Mono<AdmissionReasonDTO> findById(Integer id) {
        return admissionReasonRepository.findById(id)
                .map(this::mapToDTO);
    }

    /**
     * Obtiene el texto de la razón por ID
     */
    public Mono<String> getReasonTextById(Integer id) {
        return admissionReasonRepository.findById(id)
                .map(AdmissionReason::getReason);
    }

    /**
     * Crea una nueva razón de admisión
     */
    public Mono<AdmissionReasonDTO> create(AdmissionReasonDTO admissionReasonDTO) {
        AdmissionReason admissionReason = new AdmissionReason();
        admissionReason.setReason(admissionReasonDTO.getReason());
        // El ID se genera automáticamente en la BD, por lo que no lo asignamos
        
        return admissionReasonRepository.save(admissionReason)
                .map(this::mapToDTO);
    }

    /**
     * Mapeo de entidad a DTO
     */
    private AdmissionReasonDTO mapToDTO(AdmissionReason admissionReason) {
        AdmissionReasonDTO dto = new AdmissionReasonDTO();
        dto.setId(admissionReason.getId());
        dto.setReason(admissionReason.getReason());
        return dto;
    }
}