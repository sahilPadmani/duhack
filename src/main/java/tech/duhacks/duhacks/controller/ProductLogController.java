package tech.duhacks.duhacks.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.duhacks.duhacks.dto.ProductLogDto;
import tech.duhacks.duhacks.dto.ProductLogTotalDto;
import tech.duhacks.duhacks.service.ProductService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/logs")
public class ProductLogController {

    private final ProductService productService;

    @GetMapping("/{userId}/time/{days}")
    public ResponseEntity<List<ProductLogTotalDto>> getAllLogInDays(@PathVariable("userId") Long id,@PathVariable("days")Integer days){
        return ResponseEntity.ok(productService.getLogForTime(id,days));
    }

    @PostMapping("/log")
    public ResponseEntity<Void> addLog(@RequestBody ProductLogDto pd){
        productService.add(pd);
        return ResponseEntity.ok().build();
    }
}
