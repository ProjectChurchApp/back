package com.church.app.signup.entity;

public enum AccountStatus {
    ACTIVE,
    PENDING_PASTOR_APPROVAL,
    REJECTED,
    /** 관리자가 정지시킨 계정. 로그인은 막히지만 작성한 글과 댓글은 남는다. */
    SUSPENDED
}
