package com.bupt.tarecruit.service;

/**
 * Signals a user-facing authentication or authorisation failure.
 * Controllers catch this exception to return the caller to the login flow with
 * a readable message instead of treating it as an infrastructure error.
 */
public class AuthenticationException extends RuntimeException {
    /**
     * Creates an exception with the message shown back to the caller.
     *
     * @param message explanation of why authentication failed
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
