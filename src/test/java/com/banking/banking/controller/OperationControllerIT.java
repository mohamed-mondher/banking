package com.banking.banking.controller;


import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.entity.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static com.banking.banking.exception.Messages.ACCOUNT_NOT_FOUND;
import static com.banking.banking.exception.Messages.INSUFFICIENT_BALANCE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void should_create_deposit_operation_successfully() {
        Map<String, Object> depositRequest = Map.of(
                "accountId", 1L,
                "type", OperationType.DEPOSIT.name(),
                "amount", 50.0
        );

        ResponseEntity<OperationDto> response = restTemplate.postForEntity("/api/v1/operations", depositRequest, OperationDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(50.0, response.getBody().getAmount());
        assertEquals(OperationType.DEPOSIT.name(), response.getBody().getType());
    }

    @Test
    void should_throwError_when_accountNotFound() {
        Map<String, Object> invalidRequest = Map.of(
                "accountId", 99L,
                "type", OperationType.WITHDRAW.name(),
                "amount", 50.0
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/v1/operations", invalidRequest, Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(String.format(ACCOUNT_NOT_FOUND,99), response.getBody().get("message"));
    }

    @Test
    void should_throwError_when_withdrawal_amount_exceeds_balance() {
        Map<String, Object> withdrawRequest = Map.of(
                "accountId", 1L,
                "type", OperationType.WITHDRAW.name(),
                "amount", 200.0
        );

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/v1/operations", withdrawRequest, Map.class);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(String.format(INSUFFICIENT_BALANCE,100.5), response.getBody().get("message"));
    }

    @Test
    void should_return_operations_by_AccountId() {

        Long accountId = 1L;


        ResponseEntity<List> response = restTemplate.getForEntity("/api/v1/operations?accountId=" + accountId, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void should_throwError_accountNotFound_when_fetching_operations() {
        Long accountId = 99L;


        ResponseEntity<Map> response = restTemplate.getForEntity("/api/v1/operations?accountId=" + accountId, Map.class);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(String.format(ACCOUNT_NOT_FOUND,99L), response.getBody().get("message"));
    }
}