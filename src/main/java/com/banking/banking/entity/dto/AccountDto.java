package com.banking.banking.entity.dto;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountDto {
    private Long id;
    private List<OperationDto> operations;
    private double balance;
}
