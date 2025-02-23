package tech.duhacks.duhacks.service;

import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.dto.HealthProductDto;
import tech.duhacks.duhacks.model.HealthProduct;
import tech.duhacks.duhacks.model.MedicationSchedule;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthProductMapper {

    public HealthProductDto getHealthProductDto(HealthProduct hp){
        return new HealthProductDto(
                hp.getId(),
                hp.getName(),
                hp.getQuantity(),
                hp.getExpiryDate(),
                hp.getAmount(),
                hp.getUser().getId(),
                hp.getMedicationSchedules().stream().map(a -> a.getTime().format( DateTimeFormatter.ofPattern("HH:mm"))).collect(Collectors.toList()),
                hp.getCreatedAt().toString()
        );
    }
}
