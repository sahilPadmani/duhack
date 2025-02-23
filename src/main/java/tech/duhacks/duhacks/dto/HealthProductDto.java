package tech.duhacks.duhacks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthProductDto{
        Long id;
        String name;
        Float quantity;
        @JsonFormat(pattern = "MM/dd/yyyy")
        LocalDate expiryDate;
        Float amount;
        Long userId;
        List<String> times;
        String createdAt;
}
