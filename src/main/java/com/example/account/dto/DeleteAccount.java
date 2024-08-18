package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotNull
        @Min(1)
        private long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private long userId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

        public static Response from(AccountDTO accountDTO) {
            return Response.builder()
                    .userId(accountDTO.getUserId())
                    .accountNumber(accountDTO.getAccountNumber())
                    .unRegisteredAt(accountDTO.getUnRegisteredAt())
                    .build();
        }
    }

}
