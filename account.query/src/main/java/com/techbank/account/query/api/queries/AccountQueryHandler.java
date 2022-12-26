package com.techbank.account.query.api.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.domain.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueryHandler implements QueryHandler {

    private final AccountRepository accountRepository;

    @Override
    public List<BaseEntity> handle(FindAllAccountsQuery query) {
        Iterable<BankAccount> bankAccounts = accountRepository.findAll();
        List<BaseEntity> resultList = new ArrayList<>();
        bankAccounts.forEach(resultList::add);
        return resultList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByIdQuery query) {
        Optional<BankAccount> bankAccountOp = accountRepository.findById(query.getId());
        return bankAccountOp.isEmpty() ? null : Collections.singletonList(bankAccountOp.get());
    }

    @Override
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        Optional<BankAccount> bankAccountOp = accountRepository.findByAccountHolder(query.getAccountHolder());
        return bankAccountOp.isEmpty() ? null : Collections.singletonList(bankAccountOp.get());
    }

    @Override
    public List<BaseEntity> handle(FindAccountWithBalanceQuery query) {
        List<BankAccount> bankAccounts = query.getEqualityType().equals(EqualityType.GREATER_THAN) ?
                                                                    accountRepository.findByBalanceGreaterThan(query.getBalance()) :
                                                                    accountRepository.findByBalanceLessThan(query.getBalance());
        List<BaseEntity> resultList = new ArrayList<>();
        bankAccounts.forEach(resultList::add);
        return resultList;
    }

}
