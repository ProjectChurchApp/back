package com.church.app.signup.utils;

public class PasswordEncodeException extends RuntimeException {

    public PasswordEncodeException(String message) { super(message); }

    public PasswordEncodeException(String message, Throwable cause) { super(message, cause); }
}
