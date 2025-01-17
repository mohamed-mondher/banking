package com.banking.banking.controller;

import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.service.OperationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/v1/operations")
    public ResponseEntity<OperationDto> createOperation(@RequestBody OperationDto operationDto) {
        OperationDto createdOperation = operationService.processOperation(operationDto);
        final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/api/v1/operations").build().toUri();
        return ResponseEntity.created(location).body(createdOperation);
    }

    @GetMapping("/v1/operations")
    public ResponseEntity<List<OperationDto>> getOperationsByAccountId(@RequestParam Long accountId) {
        List<OperationDto> operations = operationService.getOperationsByAccountId(accountId);
        return ResponseEntity.ok(operations);
    }


}
