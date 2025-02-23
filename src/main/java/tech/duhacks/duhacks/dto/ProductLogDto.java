package tech.duhacks.duhacks.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLogDto {
    private Long id;
    private Long userId;
    private Long healthProductId;
    private Boolean isTaken;
}
