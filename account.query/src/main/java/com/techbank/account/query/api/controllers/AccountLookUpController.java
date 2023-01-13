package com.techbank.account.query.api.controllers;

import com.techbank.account.query.api.dto.AccountLookUpResponse;
import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.api.queries.FindAccountByHolderQuery;
import com.techbank.account.query.api.queries.FindAccountByIdQuery;
import com.techbank.account.query.api.queries.FindAccountWithBalanceQuery;
import com.techbank.account.query.api.queries.FindAllAccountsQuery;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.infrastructure.QueryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/bankAccountLookUp")
public class AccountLookUpController {

    private final Logger logger = Logger.getLogger(AccountLookUpResponse.class.getName());

    @Autowired
    private QueryDispatcher queryDispatcher;

    @GetMapping("/")
    public ResponseEntity<AccountLookUpResponse> getAllAccounts() {
        try {
            List<BankAccount> accounts = queryDispatcher.send(new FindAllAccountsQuery());
            if (CollectionUtils.isEmpty(accounts)) {
                return ResponseEntity.noContent()
                                    .build();
            }
            return ResponseEntity.ok(AccountLookUpResponse.builder()
                                                            .accounts(accounts)
                                                            .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                                                            .build());
        } catch (Exception e) {
            String safeErrorMessage = "Failed to complete get all accounts request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return ResponseEntity.internalServerError()
                                .body(new AccountLookUpResponse(safeErrorMessage));
        }
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<AccountLookUpResponse> getAccountById(@PathVariable("id") String id) {
        try {
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountByIdQuery(id));
            if (CollectionUtils.isEmpty(accounts)) {
                return ResponseEntity.noContent()
                                    .build();
            }
            return ResponseEntity.ok(AccountLookUpResponse.builder()
                                                            .accounts(accounts)
                                                            .message("Successfully returned bank account")
                                                            .build());
        } catch (Exception e) {
            String safeErrorMessage = "Failed to complete get account by id request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return ResponseEntity.internalServerError()
                                .body(new AccountLookUpResponse(safeErrorMessage));
        }
    }

    @GetMapping("/byHolder/{accountHolder}")
    public ResponseEntity<AccountLookUpResponse> getAccountByHolder(@PathVariable("accountHolder") String accountHolder) {
        try {
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountByHolderQuery(accountHolder));
            if (CollectionUtils.isEmpty(accounts)) {
                return ResponseEntity.noContent()
                                    .build();
            }
            return ResponseEntity.ok(AccountLookUpResponse.builder()
                                                            .accounts(accounts)
                                                            .message("Successfully returned bank account")
                                                            .build());
        } catch (Exception e) {
            String safeErrorMessage = "Failed to complete get account by holder request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return ResponseEntity.internalServerError()
                                .body(new AccountLookUpResponse(safeErrorMessage));
        }
    }

    @GetMapping("/withBalance/{equalityType}/{balance}")
    public ResponseEntity<AccountLookUpResponse> getAccountByHolder(@PathVariable("equalityType") EqualityType equalityType,
                                                                    @PathVariable("balance") double balance) {
        try {
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountWithBalanceQuery(equalityType, balance));
            if (CollectionUtils.isEmpty(accounts)) {
                return ResponseEntity.noContent()
                                    .build();
            }
            return ResponseEntity.ok(AccountLookUpResponse.builder()
                                                        .accounts(accounts)
                                                        .message("Successfully returned bank account")
                                                        .build());
        } catch (Exception e) {
            String safeErrorMessage = "Failed to complete get account with balance request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return ResponseEntity.internalServerError()
                                .body(new AccountLookUpResponse(safeErrorMessage));
        }
    }

}
