package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEventHandler implements EventHandler {

    private final AccountRepository accountRepository;

    @Override
    public void on(AccountOpenedEvent event) {
        accountRepository.save(BankAccount.builder()
                                        .id(event.getId())
                                        .accountHolder(event.getAccountHolder())
                                        .creationDate(event.getCreatedDate())
                                        .accountType(event.getAccountType())
                                        .balance(event.getOpeningBalance())
                                        .build());
    }

    @Override
    public void on(FundsDepositedEvent event) {
        accountRepository.findById(event.getId())
                        .ifPresent(bankAccount -> {
                            bankAccount.setBalance(bankAccount.getBalance() + event.getAmount());
                            accountRepository.save(bankAccount);
                        });
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        accountRepository.findById(event.getId())
                        .ifPresent(bankAccount -> {
                            bankAccount.setBalance(bankAccount.getBalance() - event.getAmount());
                            accountRepository.save(bankAccount);
                        });
    }

    @Override
    public void on(AccountClosedEvent event) {
        accountRepository.deleteById(event.getId());
    }

}
