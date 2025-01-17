package com.banking.banking.converter;

import com.banking.banking.entity.Account;
import com.banking.banking.entity.Operation;
import com.banking.banking.entity.dto.OperationDto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class OperationDtoConverter {

    public static OperationDto convert(Operation operation) {
        if (Objects.isNull(operation)) {
            return null;
        }
        return OperationDto.builder()
                .id(operation.getId())
                .type(operation.getType().name())
                .amount(operation.getAmount())
                .operationDate(LocalDateTime.now())
                .balance(Optional.ofNullable(operation.getAccount()).map(Account::getId).orElse(null))
                .build();

    }
}
