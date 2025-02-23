package tech.duhacks.duhacks.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.duhacks.duhacks.dto.HealthProductDto;
import tech.duhacks.duhacks.service.HealthProductService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/healthproduct")
public class HealthProductController {

    private final HealthProductService healthProductService;

    @PostMapping("/insert")
    public ResponseEntity<HealthProductDto> insert(@RequestBody HealthProductDto healthProductDto) {
        return ResponseEntity.ok(healthProductService.add(healthProductDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(healthProductService.deleteProduct(id));
    }

    @GetMapping("/user/{id}")
    public  ResponseEntity<List<HealthProductDto>> getUserProduct(@PathVariable("id")Long id){
        return ResponseEntity.ok(healthProductService.getHealthProductByUser(id));
    }

    @GetMapping("/user/lower/{id}")
    public  ResponseEntity<List<HealthProductDto>> getLowUserProduct(@PathVariable("id")Long id){
        return ResponseEntity.ok(healthProductService.getLowHealthProductByUser(id));
    }

    @GetMapping("/orders/{id}")
    public  ResponseEntity<List<HealthProductDto>> getAllUserOrder(@PathVariable("id")Long id){
        return ResponseEntity.ok(healthProductService.getAllOrder(id));
    }

}