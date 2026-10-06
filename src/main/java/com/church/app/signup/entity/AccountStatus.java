package com.church.app.signup.entity;

public enum AccountStatus {
    ACTIVE,
    PENDING_PASTOR_APPROVAL,
    REJECTED,
    /** 관리자가 정지시킨 계정. 로그인은 막히지만 작성한 글과 댓글은 남는다. */
    SUSPENDED,
    /**
     * 탈퇴 처리된 계정. 아이디를 비워 같은 아이디로 다시 가입할 수 있게 하고,
     * 작성한 글과 댓글은 "탈퇴한 사용자" 이름으로 남겨 다른 사람 기록이 깨지지 않게 한다.
     */
    WITHDRAWN
}
