package tech.duhacks.duhacks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HealthProductDto{
        Long id;
        String name;
        Float quantity;
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expiryDate;
        Float amount;
        Long userId;
        List<String> times;
        String createdAt;
}
