package tech.duhacks.duhacks.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.duhacks.duhacks.dto.ProductLogDto;
import tech.duhacks.duhacks.dto.ProductLogTotalDto;
import tech.duhacks.duhacks.exception.AuthException;
import tech.duhacks.duhacks.model.HealthProduct;
import tech.duhacks.duhacks.model.ProductLog;
import tech.duhacks.duhacks.repository.HealthProductRepo;
import tech.duhacks.duhacks.repository.ProductLogRepo;
import tech.duhacks.duhacks.repository.UserRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductLogRepo productRepo;
    private final UserRepo userRepo;
    private final HealthProductRepo healthProductRepo;

    @Transactional
    public void add(ProductLogDto pd){
//        ZonedDateTime kolkataTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        var user = userRepo.findById(pd.getUserId()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        var hp = healthProductRepo.findById(pd.getHealthProductId()).orElseThrow(()->new EntityNotFoundException("Product Not Found"));

        if(pd.getIsTaken()) {

            if (hp.getQuantity() - hp.getAmount() < 0.0f) {
                throw new AuthException("Amount is Insefisent");
            }

            healthProductRepo.updateQuantityById(hp.getId(), hp.getQuantity() - hp.getAmount());

        }

        var pl = ProductLog.builder()
                .isTaken(pd.getIsTaken())  // Setting the 'isTaken' field from the pd object
                .user(user)                // Setting the user of the log
                .healthProduct(hp)         // Setting the health product involved
                .build();                  // Building the ProductLog object

        productRepo.save(pl);
    }

    public List<ProductLogTotalDto> getLogForTime(Long userId,Integer days) {
        userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        ZonedDateTime kolkataTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        ZonedDateTime dateFromKolkata = kolkataTime.minusDays(days);
        LocalDateTime dateFrom = dateFromKolkata.toLocalDateTime();

        var res = productRepo.findAllByUserIdAndCreatedAtIsAfter(userId, dateFrom);

        Map<Long, List<ProductLog>> groupedByHealthProduct = res.stream()
                .collect(Collectors.groupingBy(product -> product.getHealthProduct().getId()));

        return groupedByHealthProduct.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    List<ProductLog> logs = entry.getValue();

                    ProductLogTotalDto dto = new ProductLogTotalDto();
                    dto.setHealthProductId(productId);
                    dto.setHealthProductName(logs.getFirst().getHealthProduct().getName());

                    long takenCount = logs.stream().filter(ProductLog::getIsTaken).count();
                    dto.setIsTakenCount(takenCount);
                    dto.setMisCount(logs.size() - takenCount);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<ProductLogTotalDto> getOneDay(Long id){
        userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        ZonedDateTime kolkataTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime now = kolkataTime.toLocalDateTime();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay(); // Start of the day
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        var res = productRepo.findAllByUserIdAndCreatedAtBetween(id,startOfDay,endOfDay);

        if(res.isEmpty()){
            List<HealthProduct> ans = healthProductRepo.findAllByUserId(id);

            Map<Long, List<HealthProduct>> groupedByHealthProduct = ans.stream()
                    .collect(Collectors.groupingBy(HealthProduct::getId));

            return groupedByHealthProduct.values().stream().map(
                    healthProducts -> {
                        var healthProduct = healthProducts.getFirst();

                        ProductLogTotalDto dto = new ProductLogTotalDto();
                        dto.setHealthProductId(healthProduct.getId());
                        dto.setHealthProductName(healthProduct.getName());

                        dto.setMisCount((long) healthProduct.getMedicationSchedules().size());
                        dto.setIsTakenCount(0L);
                        return dto;
                    }
            ).toList();
        }

        Map<String, List<ProductLog>> groupedByHealthProduct = res.stream()
                .collect(Collectors.groupingBy(productLog ->
                    productLog.getHealthProduct().getName()
                ));

        return groupedByHealthProduct.values().stream().map(
                productLogs -> {
                    var value = productLogs.getFirst();
                    ProductLogTotalDto dto = new ProductLogTotalDto();
                    dto.setHealthProductId(value.getHealthProduct().getId());
                    dto.setHealthProductName(value.getHealthProduct().getName());

                    long count = productLogs.size();
                    dto.setMisCount(value.getHealthProduct().getMedicationSchedules().size() - count);
                    dto.setIsTakenCount(count);
                    return dto;
                }
        ).toList();
    }
}
