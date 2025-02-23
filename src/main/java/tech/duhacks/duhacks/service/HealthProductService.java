package tech.duhacks.duhacks.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.dto.HealthProductDto;
import tech.duhacks.duhacks.dto.ProductLogTotalDto;
import tech.duhacks.duhacks.exception.AuthException;
import tech.duhacks.duhacks.model.HealthProduct;
import tech.duhacks.duhacks.model.MedicationSchedule;
import tech.duhacks.duhacks.repository.HealthProductRepo;
import tech.duhacks.duhacks.repository.UserRepo;
import tech.duhacks.duhacks.schedular.ExpiryEmail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HealthProductService {

    private final HealthProductRepo healthProductRepo;
    private final HealthProductMapper healthProductMapper;
    private final ExpiryEmail expiryEmail;
    private final UserRepo userRepo;
    private static final ZoneId kolkataZoneId = ZoneId.of("Asia/Kolkata");

    public HealthProductDto add(HealthProductDto hrd){
        var user = userRepo.findById(hrd.getUserId()).orElseThrow(() ->  new EntityNotFoundException("User Not Found") );

        var healthProduct  = HealthProduct.builder()
                .name(hrd.getName())
                .amount(hrd.getAmount())
                .lowQuantity(hrd.getQuantity() * 0.1f)
                .quantity(hrd.getQuantity())
                .fullQuantity(hrd.getQuantity())
                .expiryDate(hrd.getExpiryDate())
                .user(user)
                .build();

        Set<MedicationSchedule> medicationSchedules = hrd.getTimes().stream().map(time -> {
            MedicationSchedule schedule = new MedicationSchedule();
            schedule.setTime(LocalTime.parse(time));
            schedule.setHealthProduct(healthProduct); // This will link the schedule to the health product
            return schedule;
        }).collect(Collectors.toSet());

        healthProduct.setMedicationSchedules(medicationSchedules); // Associate the schedules with the health product
        var hrqSave =  healthProductMapper.getHealthProductDto(healthProductRepo.save(healthProduct));

        expiryEmail.addMedicine(healthProduct);

        return  hrqSave;
    }

    public boolean deleteProduct(Long id){
        healthProductRepo.deleteById(id);
        healthProductRepo.findById(id).orElseThrow(() -> new AuthException("Failed to delete Product"));
        expiryEmail.removeMedicine(id);
        return true;
    }

    public List<HealthProductDto> getHealthProductByUser(Long id){
        userRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("User Not Found"));

        ZonedDateTime kolkataZonedTime = ZonedDateTime.now(kolkataZoneId);
        LocalDate kolkataLocalTime = kolkataZonedTime.toLocalDate();

        var res = healthProductRepo.findAllByUserIdAndQuantityGreaterThanAndExpiryDateAfter(id,0,kolkataLocalTime);
        return res.stream().map(healthProductMapper::getHealthProductDto).toList();
    }

    public List<HealthProductDto> getAllOrder(Long id){
        userRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("User Not Found"));

        var res = healthProductRepo.findAllByUserId(id);

        return res.stream().map(healthProductMapper::getHealthProductDto).toList();
    }

    public List<HealthProductDto> getLowHealthProductByUser(Long id){
        userRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("User Not Found"));

        ZonedDateTime kolkataZonedTime = ZonedDateTime.now(kolkataZoneId);
        LocalDate kolkataLocalTime = kolkataZonedTime.toLocalDate();

        var res = healthProductRepo.findAllByUserIdAndQuantityGreaterThanAndExpiryDateAfterAndLowQuantityGreaterThanEqual(id,kolkataLocalTime);
        return res.stream().map(healthProductMapper::getHealthProductDto).toList();
    }

}
