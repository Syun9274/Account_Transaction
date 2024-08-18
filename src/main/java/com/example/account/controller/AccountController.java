package com.example.account.controller;

import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/create-account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {

        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    @DeleteMapping("/delete-account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {

        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account/{userId}")
    public List<AccountInfo> getAccount(@PathVariable long userId) {

        return accountService.getAccount(userId).stream()
                .map(accountDTO -> AccountInfo.builder()
                        .accountNumber(accountDTO.getAccountNumber())
                        .balance(accountDTO.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/get-lock")
    public String getLock() {
        return redisTestService.getLock();
    }


}
