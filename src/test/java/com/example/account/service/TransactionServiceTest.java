package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDTO;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ValidService validService;

    @InjectMocks
    private TransactionService transactionService;

    private AccountUser mockAccountUser;
    private Account mockAccount;
    private Transaction mockTransaction;
    private AccountException accountException;

    @BeforeEach
    void setUp() {
        mockAccountUser = new AccountUser();
        mockAccountUser.setId(1L);
        mockAccountUser.setUsername("Test User");

        mockAccount = Account.builder()
                .accountUser(mockAccountUser)
                .accountNumber("1234567890")
                .balance(10000L)
                .accountStatus(AccountStatus.IN_USE)
                .registeredAt(LocalDateTime.now())
                .build();

        mockTransaction = Transaction.builder()
                .transactionType(TransactionType.USE)
                .transactionResultType(TransactionResultType.SUCCESS)
                .account(mockAccount)
                .amount(1000L)
                .balanceSnapshot(mockAccount.getBalance() - 1000L)
                .transactionId(UUID.randomUUID().toString().replace("-", ""))
                .transactionAt(LocalDateTime.now())
                .build();
    }

    @Test
    void useBalance_Success() {
        // given
        long userId = 1L;
        long amount = 1000L;
        String accountNumber = "1234567890";

        given(accountUserRepository.findById(userId)).willReturn(Optional.of(mockAccountUser));
        given(accountRepository.findByAccountNumber(accountNumber)).willReturn(Optional.of(mockAccount));
        given(transactionRepository.save(any(Transaction.class))).willReturn(mockTransaction);

        // when
        TransactionDTO transactionDTO = transactionService.useBalance(userId, accountNumber, amount);

        // then
        assertNotNull(transactionDTO);
        assertEquals(TransactionResultType.SUCCESS, transactionDTO.getTransactionResultType());
        assertEquals(9000, mockAccount.getBalance());
        verify(validService, times(1)).validateUseBalance(mockAccountUser, mockAccount, amount);
    }

    @Test
    void useBalance_UserNotFound() {
        // given
        long userId = 1L;
        String accountNumber = "1234567890";
        long amount = 1000L;

        given(accountUserRepository.findById(userId)).willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(userId, accountNumber, amount));

        // then
        verify(accountRepository, never()).findByAccountNumber(anyString());
    }

    @Test
    void saveFailedUseTransaction_Success() {
        // given
        String accountNumber = "1234567890";
        long amount = 1000L;

        given(accountRepository.findByAccountNumber(accountNumber)).willReturn(Optional.of(mockAccount));

        // when
        transactionService.saveFailedUseTransaction(accountNumber, amount);

        // then
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void cancelBalance_Success() {
        // given
        String transactionId = "transactionId";
        String accountNumber = "1234567890";
        long amount = 1000L;

        given(transactionRepository.findByTransactionId(transactionId)).willReturn(Optional.of(mockTransaction));
        given(accountRepository.findByAccountNumber(accountNumber)).willReturn(Optional.of(mockAccount));
        given(transactionRepository.save(any(Transaction.class))).willReturn(mockTransaction);

        // when
        TransactionDTO transactionDTO = transactionService.cancelBalance(transactionId, accountNumber, amount);

        // then
        assertNotNull(transactionDTO);
        assertEquals(TransactionResultType.SUCCESS, transactionDTO.getTransactionResultType());
        verify(validService, times(1)).validateCancelBalance(mockTransaction, mockAccount, amount);
    }

    @Test
    void cancelBalance_TransactionNotFound() {
        // given
        String transactionId = "transactionId";
        String accountNumber = "1234567890";
        long amount = 1000L;

        given(transactionRepository.findByTransactionId(transactionId)).willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance(transactionId, accountNumber, amount));

        // then
        verify(accountRepository, never()).findByAccountNumber(anyString());
    }

    @Test
    void queryTransaction_Success() {
        // given
        String transactionId = "transactionId";

        given(transactionRepository.findByTransactionId(transactionId)).willReturn(Optional.of(mockTransaction));

        // when
        TransactionDTO transactionDTO = transactionService.queryTransaction(transactionId);

        // then
        assertNotNull(transactionDTO);
        assertEquals(mockTransaction.getTransactionId(), transactionDTO.getTransactionId());
    }
}
