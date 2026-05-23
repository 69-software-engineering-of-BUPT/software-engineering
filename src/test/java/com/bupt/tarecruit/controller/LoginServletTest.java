package com.bupt.tarecruit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.bupt.tarecruit.service.AuthService;
import com.bupt.tarecruit.service.AuthenticatedUser;
import com.bupt.tarecruit.service.AuthenticationException;

public class LoginServletTest {
    @Test
    public void doGetForwardsAnonymousUsersToLoginPage() throws Exception {
        StubAuthService authService = new StubAuthService();
        LoginServlet servlet = new LoginServlet(authService);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertTrue(dispatcher.forwarded);
        assertEquals("/jsp/login.jsp", dispatcher.path);
        assertNull(responseState.redirectLocation);
    }

    @Test
    public void doGetRedirectsAuthenticatedUsersToTheirHomePage() throws Exception {
        StubAuthService authService = new StubAuthService();
        LoginServlet servlet = new LoginServlet(authService);
        SessionState session = new SessionState()
                .withAttribute("userAccount", "MO001")
                .withAttribute("userRole", "MO");
        RequestState requestState = new RequestState("/app", session, new DispatcherState(), mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertEquals("/app/mo/home", responseState.redirectLocation);
        assertFalse(session.invalidated);
    }

    @Test
    public void doGetInvalidatesBrokenSessionsBeforeRedirectingToLogin() throws Exception {
        StubAuthService authService = new StubAuthService();
        LoginServlet servlet = new LoginServlet(authService);
        SessionState session = new SessionState()
                .withAttribute("userAccount", "MO001")
                .withAttribute("userRole", "guest");
        RequestState requestState = new RequestState("/app", session, new DispatcherState(), mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertTrue(session.invalidated);
        assertEquals("/app/login", responseState.redirectLocation);
    }

    @Test
    public void doPostStoresAuthenticatedUserInSessionAndRedirects() throws Exception {
        StubAuthService authService = new StubAuthService();
        authService.authenticatedUser = new AuthenticatedUser("TA001", "TA", "Alice");
        LoginServlet servlet = new LoginServlet(authService);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "TA001",
                "password", "secret"));
        ResponseState responseState = new ResponseState();

        servlet.doPost(requestState.proxy(), responseState.proxy());

        assertEquals("UTF-8", requestState.characterEncoding);
        assertEquals("TA001", authService.lastUserId);
        assertEquals("secret", authService.lastPassword);
        assertEquals("/app/ta/home", responseState.redirectLocation);
        assertFalse(dispatcher.forwarded);
        assertNotNull(requestState.sessionState);
        assertEquals("TA001", requestState.sessionState.attributes.get("userAccount"));
        assertEquals("TA", requestState.sessionState.attributes.get("userRole"));
        assertEquals("Alice", requestState.sessionState.attributes.get("userName"));
    }

    @Test
    public void doPostReturnsToLoginPageWithErrorMessageWhenAuthenticationFails() throws Exception {
        StubAuthService authService = new StubAuthService();
        authService.authenticationException = new AuthenticationException("Incorrect password.");
        LoginServlet servlet = new LoginServlet(authService);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "  TA001  ",
                "password", "wrong"));
        ResponseState responseState = new ResponseState();

        servlet.doPost(requestState.proxy(), responseState.proxy());

        assertEquals("UTF-8", requestState.characterEncoding);
        assertEquals("Incorrect password.", requestState.attributes.get("loginError"));
        assertEquals("TA001", requestState.attributes.get("loginUserId"));
        assertEquals("TA001", requestState.attributes.get("inputUserId"));
        assertTrue(dispatcher.forwarded);
        assertEquals("/jsp/login.jsp", dispatcher.path);
        assertNull(responseState.redirectLocation);
        assertNull(requestState.sessionState);
    }

    @Test
    public void doPostWrapsUnexpectedErrorsInServletException() throws Exception {
        StubAuthService authService = new StubAuthService();
        authService.unexpectedException = new Exception("Database unavailable");
        LoginServlet servlet = new LoginServlet(authService);
        RequestState requestState = new RequestState("/app", null, new DispatcherState(), mapOf(
                "userId", "TA001",
                "password", "secret"));

        try {
            servlet.doPost(requestState.proxy(), new ResponseState().proxy());
        } catch (ServletException ex) {
            assertEquals("Login failed", ex.getMessage());
            assertSame(authService.unexpectedException, ex.getCause());
            return;
        }

        throw new AssertionError("Expected ServletException");
    }

    private static Map<String, String> mapOf(String... values) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }

    private static final class StubAuthService extends AuthService {
        private AuthenticatedUser authenticatedUser;
        private AuthenticationException authenticationException;
        private Exception unexpectedException;
        private String lastUserId;
        private String lastPassword;

        @Override
        public AuthenticatedUser authenticate(String userId, String password) throws Exception {
            lastUserId = userId;
            lastPassword = password;
            if (authenticationException != null) {
                throw authenticationException;
            }
            if (unexpectedException != null) {
                throw unexpectedException;
            }
            return authenticatedUser;
        }
    }

    private static final class RequestState {
        private final String contextPath;
        private SessionState sessionState;
        private final DispatcherState dispatcherState;
        private final Map<String, String> parameters;
        private final Map<String, Object> attributes = new HashMap<>();
        private String characterEncoding;

        private RequestState(
                String contextPath,
                SessionState sessionState,
                DispatcherState dispatcherState,
                Map<String, String> parameters) {
            this.contextPath = contextPath;
            this.sessionState = sessionState;
            this.dispatcherState = dispatcherState;
            this.parameters = parameters;
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
                if (sessionState == null && create) {
                    sessionState = new SessionState();
                }
                return sessionState == null ? null : sessionState.proxy();
            }
            if ("getParameter".equals(name)) {
                return parameters.get(args[0]);
            }
            if ("setCharacterEncoding".equals(name)) {
                characterEncoding = (String) args[0];
                return null;
            }
            if ("setAttribute".equals(name)) {
                attributes.put((String) args[0], args[1]);
                return null;
            }
            if ("getAttribute".equals(name)) {
                return attributes.get(args[0]);
            }
            if ("getRequestDispatcher".equals(name)) {
                dispatcherState.path = (String) args[0];
                return dispatcherState.proxy();
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class DispatcherState {
        private String path;
        private boolean forwarded;
        private ServletRequest forwardedRequest;
        private ServletResponse forwardedResponse;

        private RequestDispatcher proxy() {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    RequestDispatcher.class.getClassLoader(),
                    new Class<?>[] { RequestDispatcher.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
            if ("forward".equals(method.getName())) {
                forwarded = true;
                forwardedRequest = (ServletRequest) args[0];
                forwardedResponse = (ServletResponse) args[1];
                return null;
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
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;

        private SessionState withAttribute(String name, Object value) {
            attributes.put(name, value);
            return this;
        }

        private HttpSession proxy() {
            return (HttpSession) Proxy.newProxyInstance(
                    HttpSession.class.getClassLoader(),
                    new Class<?>[] { HttpSession.class },
                    this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("getAttribute".equals(name)) {
                return attributes.get(args[0]);
            }
            if ("setAttribute".equals(name)) {
                attributes.put((String) args[0], args[1]);
                return null;
            }
            if ("invalidate".equals(name)) {
                invalidated = true;
                attributes.clear();
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
