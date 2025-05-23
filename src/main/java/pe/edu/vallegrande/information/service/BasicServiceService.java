package pe.edu.vallegrande.information.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.vallegrande.information.model.BasicService;
import pe.edu.vallegrande.information.repository.BasicServiceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BasicServiceService {
    
    private final BasicServiceRepository basicServiceRepository;
    
    @Autowired
    public BasicServiceService(BasicServiceRepository basicServiceRepository) {
        this.basicServiceRepository = basicServiceRepository;
    }
    
    public Flux<BasicService> findAll() {
        return basicServiceRepository.findAll();
    }
    
    public Mono<BasicService> findById(Integer id) {
        return basicServiceRepository.findById(id);
    }
    
    public Mono<BasicService> save(BasicService basicService) {
        return basicServiceRepository.save(basicService);
    }
    
    public Mono<BasicService> update(Integer id, BasicService basicService) {
        return basicServiceRepository.findById(id)
                .flatMap(existingService -> {
                    updateFromDTO(existingService, basicService);
                    return basicServiceRepository.save(existingService);
                });
    }
    
    public Mono<Void> delete(Integer id) {
        return basicServiceRepository.deleteById(id);
    }
    
    private void updateFromDTO(BasicService service, BasicService dto) {
        if (service == null || dto == null) {
            return;
        }
        
        service.setWaterService(dto.getWaterService());
        service.setServDrain(dto.getServDrain());
        service.setServLight(dto.getServLight());
        service.setServCable(dto.getServCable());
        service.setServGas(dto.getServGas());
        service.setArea(dto.getArea());
        service.setReferenceLocation(dto.getReferenceLocation());
        service.setResidue(dto.getResidue());
        service.setPublicLighting(dto.getPublicLighting());
        service.setSecurity(dto.getSecurity());
        service.setMaterial(dto.getMaterial());
        service.setFeeding(dto.getFeeding());
        service.setEconomic(dto.getEconomic());
        service.setSpiritual(dto.getSpiritual());
        service.setSocialCompany(dto.getSocialCompany());
        service.setGuideTip(dto.getGuideTip());
    }
}
