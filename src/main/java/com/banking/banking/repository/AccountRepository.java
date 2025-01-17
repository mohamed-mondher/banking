package com.banking.banking.repository;

import com.banking.banking.entity.Account;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        Account account = buildStaticAccount();
        accounts.put(account.getId(), account);
    }


    private Account buildStaticAccount() {
        return Account.builder()
                .id(1L)
                .balance(100.5)
                .operations(new ArrayList<>())
                .build();
    }


    public Optional<Account> findAccountById(Long accountId) {
       return Optional.ofNullable(accounts.get(accountId));
    }
}
