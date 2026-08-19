package com.church.app.signup.entity;

public enum Role {
    ADMIN("관리자"),
    PASTOR("목사"),
    MEMBER("성도");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Role fromLabel(String label) {
        for (Role role : values()) {
            if (role.label.equals(label)) {
                return role;
            }
        }
        throw new IllegalArgumentException("알 수 없는 역할입니다: " + label);
    }
}
