package com.techbank.account.cmd.domain;

import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.cqrs.core.domain.AggregateRoot;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
    This class is going to be in charge of handling and keeping consistency in an specific account
    TODO: Complete a precise description for this class
 */
@NoArgsConstructor
public class AccountAggregate extends AggregateRoot {

    private boolean isActive;
    private double currentBalance;

    public AccountAggregate(OpenAccountCommand command) {
        raiseEvent(AccountOpenedEvent.builder()
                                    .id(command.getId())
                                    .accountHolder(command.getAccountHolder())
                                    .createdDate(new Date())
                                    .accountType(command.getAccountType())
                                    .openingBalance(command.getOpeningBalance())
                                    .build());
    }

    public double getCurrentBalance() {
        return this.currentBalance;
    }

    public boolean getActive() {
        return isActive;
    }

    public void apply(AccountOpenedEvent event) {
        this.id = event.getId();
        this.isActive = true;
        this.currentBalance = event.getOpeningBalance();
    }

    public void depositFunds(double amount) {
        if (!this.isActive) {
            throw new IllegalStateException("Funds cannot be deposited into a closed account");
        }
        if (amount <= 0) {
            throw new IllegalStateException("The deposit amount must be greater than 0");
        }
        raiseEvent(FundsDepositedEvent.builder()
                                    .id(this.id)
                                    .amount(amount)
                                    .build());
    }

    public void apply(FundsDepositedEvent event) {
        this.id = event.getId();
        this.currentBalance += event.getAmount();
    }

    public void withdrawFunds(double amount) {
        if (!this.isActive) {
            throw new IllegalStateException("Funds cannot be withdrawn from a closed account");
        }
        raiseEvent(FundsWithdrawnEvent.builder()
                                    .id(this.id)
                                    .amount(amount)
                                    .build());
    }

    public void apply(FundsWithdrawnEvent event) {
        this.id = event.getId();
        this.currentBalance -= event.getAmount();
    }

    public void closeAccount() {
        if (!this.isActive) {
            throw new IllegalStateException("This bank account has already been closed");
        }
        raiseEvent(AccountClosedEvent.builder()
                                    .id(this.id)
                                    .build());
    }

    public void apply(AccountClosedEvent event) {
        this.id = event.getId();
        this.isActive = false;
    }

}
