package com.example.account.dto;

import com.example.account.domain.Transaction;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private String accountNumber;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private long amount;
    private long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactionAt;

    public static TransactionDTO fromEntity(Transaction transaction) {

        return TransactionDTO.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactionAt(transaction.getTransactionAt())
                .build();
    }
}
