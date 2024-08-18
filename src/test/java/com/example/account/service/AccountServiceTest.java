package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDTO;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.AccountRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountNumberService accountNumberService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_Success() {

        // given
        long userId = 1L;
        long initialBalance = 1000L;
        String generatedAccountNumber = "1234567890";
        AccountUser mockAccountUser = new AccountUser();
        mockAccountUser.setId(userId);

        // 모킹 설정
        given(accountUserRepository.findById(userId))
                .willReturn(Optional.of(mockAccountUser));
        given(accountNumberService.generateAccountNumber())
                .willReturn(generatedAccountNumber);

        // Account 객체 생성 후 save 시 반환되도록 설정
        Account savedAccount = Account.builder()
                .accountUser(mockAccountUser)
                .accountStatus(AccountStatus.IN_USE)
                .accountNumber(generatedAccountNumber)
                .balance(initialBalance)
                .registeredAt(LocalDateTime.now())
                .build();

        given(accountRepository.save(any(Account.class))).willReturn(savedAccount);

        // when
        AccountDTO accountDTO = accountService.createAccount(userId, initialBalance);

        // then
        assertEquals(accountDTO.getUserId(), userId);
        assertEquals(accountDTO.getAccountNumber(), generatedAccountNumber);
    }

    @Test
    @DisplayName("사용자 없음")
    void createAccount_UserNotFound() {

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("게정 당 소유 가능한 최대 계좌 수 초과")
    void createAccount_maxAccountIs10() {

        AccountUser user = AccountUser.builder()
                .id(15L)
                .username("testUser")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
    }
}