package com.banking.banking.converter;

import com.banking.banking.entity.Account;
import com.banking.banking.entity.Operation;
import com.banking.banking.entity.dto.OperationDto;
import com.banking.banking.entity.enums.OperationType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class OperationConverter {


    public static Operation convert(OperationDto operation) {
        if (Objects.isNull(operation)) {
            return null;
        }
        return Operation.builder()
                .type(OperationType.valueOf(operation.getType()))
                .amount(operation.getAmount())
                .operationDate(LocalDateTime.now())
                .build();

    }
}
