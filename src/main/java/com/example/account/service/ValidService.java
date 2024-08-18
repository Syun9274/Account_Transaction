package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.account.type.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidService {

    private final AccountRepository accountRepository;

    // AccountService
    void validateCreateAccount(AccountUser accountUser) {

        if (accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    void validateDeleteAccount(AccountUser accountUser, Account account) {

        if (accountUser.getId() != account.getAccountUser().getId()) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }


    // TransactionService
    void validateUseBalance(
            AccountUser user, Account account, long amount
    ) {

        if (user.getId() != account.getAccountUser().getId()) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    void validateCancelBalance(
            Transaction transaction, Account account, long amount
    ) {

        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if (transaction.getAmount() != amount) {
            throw new AccountException(CANCEL_MOST_FULLY);
        }
        if (transaction.getTransactionAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(TOO_OLD_OLDER_TO_CANCEL);
        }
    }
}
