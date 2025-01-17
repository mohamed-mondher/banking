package com.banking.banking.service;

import com.banking.banking.entity.Account;
import com.banking.banking.entity.Operation;
import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.entity.enums.OperationType;
import com.banking.banking.exception.AccountNotFoundException;
import com.banking.banking.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.banking.banking.exception.Messages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {


    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private OperationService operationService;

    private Account staticData() {
        // Create a mock account
        Account account = Account.builder()
                .id(1L)
                .balance(100.5)
                .operations(new ArrayList<>())
                .build();

        Operation withdrawOperation = new Operation(1L, account, OperationType.WITHDRAW, 50, LocalDateTime.now());
        Operation depositOperation = new Operation(2L, account, OperationType.DEPOSIT, 100, LocalDateTime.now());
        account.getOperations().addAll(List.of(withdrawOperation, depositOperation));

        return account;
    }

    @Nested
    @DisplayName("process operation")
    class ProcessOperation {

        @Test
        void should_throwError_when_account_does_notExist() {
            //given
            Mockito.when(accountRepository.findAccountById(99L)).thenReturn(Optional.empty());

            OperationDto deposit = OperationDto.builder()
                    .accountId(99L)
                    .type(OperationType.DEPOSIT.name())
                    .amount(50.0)
                    .build();

            Exception exception = Assertions.assertThrows(AccountNotFoundException.class, () -> operationService.processOperation(deposit));
            assertEquals(String.format(ACCOUNT_NOT_FOUND, deposit.getAccountId()), exception.getMessage());
        }


        @Test
        void should_throwError_when_withdraw_amount_exceeds_balance() {
            Account account = staticData();
            Mockito.when(accountRepository.findAccountById(1L)).thenReturn(Optional.ofNullable(account));

            OperationDto withdraw = OperationDto.builder()
                    .accountId(1L)
                    .type(OperationType.WITHDRAW.name())
                    .amount(200.0)
                    .build();

            Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> operationService.processOperation(withdraw));
            assertEquals(String.format(INSUFFICIENT_BALANCE, 100.5), exception.getMessage());
        }

        @Test
        void should_throwError_when_operation_amount_is_0() {
            Account account = staticData();
            Mockito.when(accountRepository.findAccountById(1L)).thenReturn(Optional.ofNullable(account));

            OperationDto withdraw = OperationDto.builder()
                    .accountId(1L)
                    .type(OperationType.WITHDRAW.name())
                    .amount(0)
                    .build();

            Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> operationService.processOperation(withdraw));
            assertEquals(AMOUNT_MUST_BE_GREATER_THAN_0, exception.getMessage());
        }


        @Test
        void should_perform_deposit_when_accountExists_and_amountIsGtZero() {

            Account account = staticData();
            Mockito.when(accountRepository.findAccountById(1L)).thenReturn(Optional.ofNullable(account));

            OperationDto deposit = OperationDto.builder()
                    .accountId(1L)
                    .type(OperationType.DEPOSIT.name())
                    .amount(50.0)
                    .build();

            OperationDto result = operationService.processOperation(deposit);

            assertEquals(150.5, account.getBalance() + deposit.getAmount());
            assertEquals(OperationType.DEPOSIT.name(), result.getType());
        }

        @Test
        void should_perform_withdraw_when_accountExists_and_amountIsGtZero_and_balanceGteAmount() {

            Account account = staticData();
            Mockito.when(accountRepository.findAccountById(1L)).thenReturn(Optional.ofNullable(account));

            OperationDto withdraw = OperationDto.builder()
                    .accountId(1L)
                    .type(OperationType.WITHDRAW.name())
                    .amount(50.0)
                    .build();

            OperationDto result = operationService.processOperation(withdraw);

            assertEquals(50.5, account.getBalance() - withdraw.getAmount());
            assertEquals(OperationType.WITHDRAW.name(), result.getType());
        }

    }


    @Nested
    @DisplayName("get all operation by account id")
    class getOperations {
        @Test
        void should_return_operations_given_accountId_when_accountExists() {
            Account account = staticData();
            Mockito.when(accountRepository.findAccountById(1L)).thenReturn(Optional.ofNullable(account));

            List<OperationDto> result = operationService.getOperationsByAccountId(account.getId());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
            assertEquals(50, result.get(0).getAmount());
            assertEquals(100, result.get(1).getAmount());
            assertEquals(OperationType.WITHDRAW.name(), result.get(0).getType());
            assertEquals(OperationType.DEPOSIT.name(), result.get(1).getType());

        }

        @Test
        void should_throwError_when_accountNotFound() {
            // Arrange
            Long accountId = 2L;

            AccountNotFoundException exception = Assertions.assertThrows(AccountNotFoundException.class, () ->
                    operationService.getOperationsByAccountId(accountId));

            assertEquals(String.format(ACCOUNT_NOT_FOUND, accountId), exception.getMessage());
        }
    }

}
