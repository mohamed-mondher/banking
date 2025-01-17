package com.banking.banking.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Account {

    private Long id;
    private List<Operation> operations;
    private double balance;
}
