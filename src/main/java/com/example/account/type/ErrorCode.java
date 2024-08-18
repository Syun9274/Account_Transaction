package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST("잘못된 요청"),
    USER_NOT_FOUND("사용자 없음"),
    USER_ID_MINUS("유저 아이디 음수"),
    ACCOUNT_NOT_FOUND("계좌 없음"),
    AMOUNT_EXCEED_BALANCE("잔액 부족"),
    USER_ACCOUNT_UN_MATCH("사용자와 계좌 소유주 불일치"),
    ACCOUNT_ALREADY_UNREGISTERED("이미 해지된 계좌"),
    BALANCE_NOT_EMPTY("계좌에 잔액 존재"),
    MAX_ACCOUNT_PER_USER_10("최대 계좌 수 초과(10개)"),
    TRANSACTION_NOT_FOUND("거래 내역 없음"),
    TRANSACTION_ACCOUNT_UN_MATCH("거래 내역과 계좌 불일치"),
    CANCEL_MOST_FULLY("거래 내역 부분 취소 불가"),
    TOO_OLD_OLDER_TO_CANCEL("취소 가능 기간 만료(1년)");

    private final String description;
}
