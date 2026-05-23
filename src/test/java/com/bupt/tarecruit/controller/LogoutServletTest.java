package com.bupt.tarecruit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

public class LogoutServletTest {
    @Test
    public void doGetInvalidatesExistingSessionAndRedirectsToLogin() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        SessionState session = new SessionState();
        RequestState requestState = new RequestState("/app", session);
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertTrue(session.invalidated);
        assertEquals("/app/login", responseState.redirectLocation);
    }

    @Test
    public void doGetRedirectsToLoginWhenSessionDoesNotExist() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        RequestState requestState = new RequestState("/app", null);
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertFalse(requestState.sessionRequestedWithCreation);
        assertEquals("/app/login", responseState.redirectLocation);
    }

    @Test
    public void doPostDelegatesToDoGet() throws Exception {
        LogoutServlet servlet = new LogoutServlet();
        SessionState session = new SessionState();
        RequestState requestState = new RequestState("/app", session);
        ResponseState responseState = new ResponseState();

        servlet.doPost(requestState.proxy(), responseState.proxy());

        assertTrue(session.invalidated);
        assertEquals("/app/login", responseState.redirectLocation);
    }

    private static final class RequestState {
        private final String contextPath;
        private final SessionState sessionState;
        private boolean sessionRequestedWithCreation;

        private RequestState(String contextPath, SessionState sessionState) {
            this.contextPath = contextPath;
            this.sessionState = sessionState;
        }

        private HttpServletRequest proxy() {
            return (HttpServletRequest) Proxy.newProxyInstance(
                    HttpServletRequest.class.getClassLoader(),
                    new Class<?>[] { HttpServletRequest.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("getContextPath".equals(name)) {
                return contextPath;
            }
            if ("getSession".equals(name)) {
                boolean create = args == null || args.length == 0 || (Boolean) args[0];
                sessionRequestedWithCreation = create;
                if (sessionState == null) {
                    return null;
                }
                return sessionState.proxy();
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class ResponseState {
        private String redirectLocation;

        private HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    HttpServletResponse.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
            if ("sendRedirect".equals(method.getName())) {
                redirectLocation = (String) args[0];
                return null;
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class SessionState implements InvocationHandler {
        private boolean invalidated;

        private HttpSession proxy() {
            return (HttpSession) Proxy.newProxyInstance(
                    HttpSession.class.getClassLoader(),
                    new Class<?>[] { HttpSession.class },
                    this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("invalidate".equals(method.getName())) {
                invalidated = true;
                return null;
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (boolean.class.equals(returnType)) {
            return false;
        }
        if (byte.class.equals(returnType)) {
            return (byte) 0;
        }
        if (short.class.equals(returnType)) {
            return (short) 0;
        }
        if (int.class.equals(returnType)) {
            return 0;
        }
        if (long.class.equals(returnType)) {
            return 0L;
        }
        if (float.class.equals(returnType)) {
            return 0f;
        }
        if (double.class.equals(returnType)) {
            return 0d;
        }
        if (char.class.equals(returnType)) {
            return '\0';
        }
        return null;
    }
}
