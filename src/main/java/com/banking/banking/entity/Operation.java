package com.banking.banking.entity;

import com.banking.banking.entity.enums.OperationType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Operation {

    private Long id;
    private Account account;
    private OperationType type;
    private double amount;
    private LocalDateTime operationDate;
}
