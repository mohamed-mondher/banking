package com.banking.banking.entity.dto;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OperationDto {

    private Long id;
    private double balance;
    private String type;
    private double amount;
    private LocalDateTime operationDate;
    private Long accountId;
}
