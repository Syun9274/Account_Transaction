package com.example.account.controller;

import com.example.account.dto.CancelBalance;
import com.example.account.dto.TransactionDTO;
import com.example.account.dto.UseBalance;
import com.example.account.service.TransactionService;
import com.example.account.type.TransactionResultType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.example.account.type.TransactionResultType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successUseBalance() throws Exception {

        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDTO.builder()
                        .accountNumber("1000000000")
                        .transactionAt(LocalDateTime.now())
                        .amount(12345L)
                        .transactionId("transactionId")
                        .transactionResultType(SUCCESS)
                        .build());

        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UseBalance.Request(1L, "2000000000", 3000L)
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.accountNumber").value("1000000000"))
                .andExpect(jsonPath("@.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("@.transactionId").value("transactionId"))
                .andExpect(jsonPath("@.amount").value(12345));
    }

    @Test
    void successCancelBalance() throws Exception {

        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDTO.builder()
                        .accountNumber("1000000000")
                        .transactionAt(LocalDateTime.now())
                        .amount(12345L)
                        .transactionId("transactionId")
                        .transactionResultType(SUCCESS)
                        .build());

        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("transactionId", "1000000000", 12345L)
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(12345));
    }
}