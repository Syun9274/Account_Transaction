package com.example.account.service;

import com.example.account.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountNumberService {

    private final AccountRepository accountRepository;

    public String generateAccountNumber() {
        Random random = new Random();
        String newAccountNumber;

        do {
            // 0에서 9,999,999,999까지의 숫자 생성 (10자리 숫자)
            long randomNumber = random.nextLong(10000000000L);

            // 10자리 숫자로 변환, 빈 자리에 0채우기
            newAccountNumber = String.format("%010d", randomNumber);

            // 중복 체크
        } while (accountRepository.existsByAccountNumber(newAccountNumber));

        return newAccountNumber;
    }
}
