package com.banking.banking.controller;


import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.entity.enums.OperationType;
import com.banking.banking.exception.AccountNotFoundException;
import com.banking.banking.service.OperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.banking.banking.exception.Messages.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

    private static final String API_URI = "/api/v1/operations";

    @MockitoBean
    private OperationService operationService;

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private OperationController operationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("process operation")
    class ProcessOperation {
        @Test
        void should_create_operation_and_return_createdStatus() throws Exception {

            OperationDto operationDto = new OperationDto(1L, 50.0, OperationType.DEPOSIT.name(), 50.0, LocalDateTime.now(), 1L);
            when(operationService.processOperation(any(OperationDto.class))).thenReturn(operationDto);

            mockMvc.perform(post(API_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"accountId\": 1, \"amount\": 50.0, \"type\": \"DEPOSIT\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(1))
                    .andExpect(jsonPath("$.amount").value(50.0))
                    .andExpect(jsonPath("$.type").value("DEPOSIT"));
        }

        @Test
        void should_throwError_badRequest_when_amount_is_greater_than_balance() throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Arrange
            OperationDto operationDto = new OperationDto(1L, 50.0, OperationType.WITHDRAW.name(), 500.0, LocalDateTime.now(), 1L);

            when(operationService.processOperation(any(OperationDto.class)))
                    .thenThrow(new IllegalArgumentException(String.format(INSUFFICIENT_BALANCE, 100.0)));

            mockMvc.perform(post(API_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(operationDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                    .andExpect(result ->
                            assertEquals(String.format(INSUFFICIENT_BALANCE, 100.0),
                                    result.getResolvedException().getMessage()));

            verify(operationService, times(1)).processOperation(any(OperationDto.class));
        }


        @Test
        void should_throwError_badRequest_when_operation_amount_is_0() throws Exception {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Arrange
            OperationDto operationDto = new OperationDto(1L, 50.0, OperationType.WITHDRAW.name(), -1.0, LocalDateTime.now(), 1L);

            when(operationService.processOperation(any(OperationDto.class)))
                    .thenThrow(new IllegalArgumentException(AMOUNT_MUST_BE_GREATER_THAN_0));

            mockMvc.perform(post(API_URI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(operationDto))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                    .andExpect(result ->
                            assertEquals(AMOUNT_MUST_BE_GREATER_THAN_0,
                                    result.getResolvedException().getMessage()));

            verify(operationService, times(1)).processOperation(any(OperationDto.class));
        }

    }

    @Nested
    @DisplayName("get all operation by account id")
    class getOperations {
        @Test
        void should_return_operations_byAccountId_success() throws Exception {
            // Arrange
            Long accountId = 1L;
            List<OperationDto> operations = Arrays.asList(
                    new OperationDto(1L, 50, OperationType.WITHDRAW.name(), 50, LocalDateTime.now(), 1L),
                    new OperationDto(2L, 100, OperationType.DEPOSIT.name(), 100, LocalDateTime.now(), 1L)
            );

            when(operationService.getOperationsByAccountId(accountId)).thenReturn(operations);


            MvcResult mvcResult = mockMvc.perform(get(API_URI)
                            .param("accountId", accountId.toString())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
            verify(operationService).getOperationsByAccountId(accountId);
        }

        @Test
        void should_throwError_accountNotFound() throws Exception {
            Long accountId = 99L;

            when(operationService.getOperationsByAccountId(accountId))
                    .thenThrow(new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));


            mockMvc.perform(get(API_URI)
                            .param("accountId", accountId.toString())
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(result ->
                            assertEquals(String.format(ACCOUNT_NOT_FOUND, accountId),
                                    result.getResolvedException().getMessage()));


            verify(operationService, times(1)).getOperationsByAccountId(accountId);
        }

    }

}

