package tech.duhacks.duhacks.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogTotalDto {
    private String healthProductName;
    private Long healthProductId;
    private Long isTakenCount;
    private Long MisCount;
}
