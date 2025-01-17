package com.banking.banking.service;

import com.banking.banking.converter.OperationConverter;
import com.banking.banking.converter.OperationDtoConverter;
import com.banking.banking.entity.Account;
import com.banking.banking.entity.Operation;
import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.entity.enums.OperationType;
import com.banking.banking.exception.AccountNotFoundException;
import com.banking.banking.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.banking.banking.exception.Messages.*;

@AllArgsConstructor
@Service
public class OperationService {

    private final AccountRepository accountRepository;


    public OperationDto processOperation(OperationDto operationDto) {
        // Get on map
        Account account = accountRepository.findAccountById(operationDto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, operationDto.getAccountId())));

        validateOperation(operationDto, account);

        if (operationDto.getType().equals(OperationType.WITHDRAW.name())) {
            account.setBalance(account.getBalance() - operationDto.getBalance());
        } else {
            account.setBalance(account.getBalance() + operationDto.getBalance());
        }

        Operation operation = OperationConverter.convert(operationDto);
        operation.setAccount(account);
        account.getOperations().add(operation);

        return operationDto;
    }

    public List<OperationDto> getOperationsByAccountId(Long accountId) {
        Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));

        return account.getOperations().stream()
                .map(OperationDtoConverter::convert)
                .collect(Collectors.toList());
    }

    private void validateOperation(OperationDto operationDto, Account account) {
        if (operationDto.getAmount() <= 0) {
            throw new IllegalArgumentException(AMOUNT_MUST_BE_GREATER_THAN_0);
        }

        if (operationDto.getType().equals(OperationType.WITHDRAW.name())) {
            if (account.getBalance() <= operationDto.getAmount()) {
                throw new IllegalArgumentException(String.format(INSUFFICIENT_BALANCE, account.getBalance()));
            }
        }
    }


}
