package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDTO;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountNumberService accountNumberService;
    private final ValidService validService;

    @Transactional
    public AccountDTO createAccount(long userId, long initialBalance) {

        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validService.validateCreateAccount(accountUser);

        String newAccountNumber = accountNumberService.generateAccountNumber();

        return AccountDTO.fromEntity(
                accountRepository.save(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()));
    }

    @Transactional
    public List<AccountDTO> getAccount(long userId) {

        if (userId < 0) {
            throw new AccountException(USER_ID_MINUS);
        }

        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return accounts.stream()
                .map(AccountDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountDTO deleteAccount(long userId, String accountNumber) {

        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validService.validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        return AccountDTO.fromEntity(account);
    }
}
