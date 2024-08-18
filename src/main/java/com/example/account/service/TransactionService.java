package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDTO;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.account.type.ErrorCode.*;
import static com.example.account.type.TransactionResultType.FAIL;
import static com.example.account.type.TransactionResultType.SUCCESS;
import static com.example.account.type.TransactionType.CANCEL;
import static com.example.account.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private ValidService validService;

    @Transactional
    public TransactionDTO useBalance(
            long userId, String accountNumber, long amount
    ) {

        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validService.validateUseBalance(user, account, amount);

        account.useBalance(amount);

        return TransactionDTO.fromEntity(
                saveAndGetTransaction(USE, SUCCESS, account, amount));
    }

    @Transactional
    public void saveFailedUseTransaction(
            String accountNumber, long amount
    ) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(USE, FAIL, account, amount);
    }

    private Transaction saveAndGetTransaction(
            TransactionType transactionType,
            TransactionResultType transactionResultType,
            Account account,
            long amount
    ) {

        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID()
                                .toString()
                                .replace("-", ""))
                        .transactionAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional
    public TransactionDTO cancelBalance(
            String transactionId, String accountNumber, long amount
    ) {

        Transaction transaction =
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new AccountException(TRANSACTION_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validService.validateCancelBalance(transaction, account, amount);

        account.cancelBalance(amount);

        return TransactionDTO.fromEntity(
                saveAndGetTransaction(CANCEL, SUCCESS, account, amount)
        );
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, long amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(CANCEL, FAIL, account, amount);
    }

    public TransactionDTO queryTransaction(String transactionId) {

        return TransactionDTO.fromEntity(
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new AccountException(TRANSACTION_NOT_FOUND)));
    }
}
