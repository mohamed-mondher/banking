package com.banking.banking.exception;

import org.springframework.stereotype.Component;

@Component
public class Messages {

    public static final String ACCOUNT_NOT_FOUND = "Account not found for ID: %d";
    public static final String AMOUNT_MUST_BE_GREATER_THAN_0 = "Amount must be greater than 0.";

    public static final String INSUFFICIENT_BALANCE = "Insufficient balance for withdrawal. Your current balance is : %f";
}
