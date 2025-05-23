package pe.edu.vallegrande.information.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.vallegrande.information.model.HousingDetails;
import pe.edu.vallegrande.information.repository.HousingDetailsRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HousingDetailsService {
    
    private final HousingDetailsRepository housingDetailsRepository;
    
    @Autowired
    public HousingDetailsService(HousingDetailsRepository housingDetailsRepository) {
        this.housingDetailsRepository = housingDetailsRepository;
    }
    
    public Flux<HousingDetails> findAll() {
        return housingDetailsRepository.findAll();
    }
    
    public Mono<HousingDetails> findById(Integer id) {
        return housingDetailsRepository.findById(id);
    }
    
    public Mono<HousingDetails> save(HousingDetails housingDetails) {
        return housingDetailsRepository.save(housingDetails);
    }
    
    public Mono<HousingDetails> update(Integer id, HousingDetails housingDetails) {
        return housingDetailsRepository.findById(id)
                .flatMap(existingHousing -> {
                    updateFromDTO(existingHousing, housingDetails);
                    return housingDetailsRepository.save(existingHousing);
                });
    }
    
    public Mono<Void> delete(Integer id) {
        return housingDetailsRepository.deleteById(id);
    }
    
    private void updateFromDTO(HousingDetails housing, HousingDetails dto) {
        if (housing == null || dto == null) {
            return;
        }
        
        housing.setTypeOfHousing(dto.getTypeOfHousing());
        housing.setHousingMaterial(dto.getHousingMaterial());
        housing.setHousingSecurity(dto.getHousingSecurity());
        housing.setHomeEnvironment(dto.getHomeEnvironment());
        housing.setBedroomNumber(dto.getBedroomNumber());
        housing.setHabitability(dto.getHabitability());
        housing.setNumberRooms(dto.getNumberRooms());
        housing.setNumberOfBedrooms(dto.getNumberOfBedrooms());
        housing.setHabitabilityBuilding(dto.getHabitabilityBuilding());
    }
}
