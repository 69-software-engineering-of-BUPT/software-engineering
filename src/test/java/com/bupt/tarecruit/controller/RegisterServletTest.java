package com.bupt.tarecruit.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.repository.UserRepository;

public class RegisterServletTest {
    @Test
    public void doGetForwardsAnonymousUsersToRegisterPage() throws Exception {
        RegisterServlet servlet = new RegisterServlet(new StubUserRepository());
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertTrue(dispatcher.forwarded);
        assertEquals("/jsp/register.jsp", dispatcher.path);
        assertNull(responseState.redirectLocation);
    }

    @Test
    public void doGetRedirectsAuthenticatedUsersToTheirHomePage() throws Exception {
        RegisterServlet servlet = new RegisterServlet(new StubUserRepository());
        SessionState session = new SessionState()
                .withAttribute("userAccount", "TA001")
                .withAttribute("userRole", "TA");
        RequestState requestState = new RequestState("/app", session, new DispatcherState(), mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertEquals("/app/ta/home", responseState.redirectLocation);
        assertFalse(session.invalidated);
    }

    @Test
    public void doGetInvalidatesBrokenSessionBeforeRedirectingToLogin() throws Exception {
        RegisterServlet servlet = new RegisterServlet(new StubUserRepository());
        SessionState session = new SessionState()
                .withAttribute("userAccount", "TA001")
                .withAttribute("userRole", "guest");
        RequestState requestState = new RequestState("/app", session, new DispatcherState(), mapOf());
        ResponseState responseState = new ResponseState();

        servlet.doGet(requestState.proxy(), responseState.proxy());

        assertTrue(session.invalidated);
        assertEquals("/app/login", responseState.redirectLocation);
    }

    @Test
    public void doPostRejectsBlankFields() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "   ",
                "name", "",
                "password", "secret1",
                "confirmPassword", "secret1"));
        ResponseState responseState = new ResponseState();

        servlet.doPost(requestState.proxy(), responseState.proxy());

        assertEquals("UTF-8", requestState.characterEncoding);
        assertEquals("All fields are required.", requestState.attributes.get("error"));
        assertTrue(dispatcher.forwarded);
        assertEquals("/jsp/register.jsp", dispatcher.path);
        assertNull(responseState.redirectLocation);
        assertNull(userRepository.savedUser);
    }

    @Test
    public void doPostRejectsInvalidUserId() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "bad id",
                "name", "Alice",
                "password", "secret1",
                "confirmPassword", "secret1"));

        servlet.doPost(requestState.proxy(), new ResponseState().proxy());

        assertEquals("User ID may contain only letters, numbers, and underscores.",
                requestState.attributes.get("error"));
        assertEquals("bad id", requestState.attributes.get("prevUserId"));
        assertEquals("Alice", requestState.attributes.get("prevName"));
        assertTrue(dispatcher.forwarded);
        assertNull(userRepository.savedUser);
    }

    @Test
    public void doPostRejectsMismatchedPasswords() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "TA100",
                "name", "Alice",
                "password", "secret1",
                "confirmPassword", "secret2"));

        servlet.doPost(requestState.proxy(), new ResponseState().proxy());

        assertEquals("Passwords do not match.", requestState.attributes.get("error"));
        assertEquals("TA100", requestState.attributes.get("prevUserId"));
        assertEquals("Alice", requestState.attributes.get("prevName"));
        assertTrue(dispatcher.forwarded);
        assertNull(userRepository.savedUser);
    }

    @Test
    public void doPostRejectsShortPassword() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "TA100",
                "name", "Alice",
                "password", "12345",
                "confirmPassword", "12345"));

        servlet.doPost(requestState.proxy(), new ResponseState().proxy());

        assertEquals("Password must be at least 6 characters.", requestState.attributes.get("error"));
        assertEquals("TA100", requestState.attributes.get("prevUserId"));
        assertEquals("Alice", requestState.attributes.get("prevName"));
        assertTrue(dispatcher.forwarded);
        assertNull(userRepository.savedUser);
    }

    @Test
    public void doPostRejectsDuplicateUserId() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        userRepository.existingUser = user("TA100", "TA", "Existing User", "secret1");
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "TA100",
                "name", "Alice",
                "password", "secret1",
                "confirmPassword", "secret1"));

        servlet.doPost(requestState.proxy(), new ResponseState().proxy());

        assertEquals("TA100", userRepository.lastLookupUserId);
        assertEquals("User ID \"TA100\" is already taken. Please choose another.",
                requestState.attributes.get("error"));
        assertEquals("TA100", requestState.attributes.get("prevUserId"));
        assertEquals("Alice", requestState.attributes.get("prevName"));
        assertTrue(dispatcher.forwarded);
        assertNull(userRepository.savedUser);
    }

    @Test
    public void doPostShowsErrorWhenSavingUserFails() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        userRepository.saveException = new IOException("disk full");
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "TA100",
                "name", "Alice",
                "password", "secret1",
                "confirmPassword", "secret1"));

        servlet.doPost(requestState.proxy(), new ResponseState().proxy());

        assertEquals("Failed to create account. Please try again.", requestState.attributes.get("error"));
        assertEquals("TA100", requestState.attributes.get("prevUserId"));
        assertEquals("Alice", requestState.attributes.get("prevName"));
        assertTrue(dispatcher.forwarded);
        assertNotNull(userRepository.savedUser);
    }

    @Test
    public void doPostCreatesTaAccountAndShowsSuccessMessage() throws Exception {
        StubUserRepository userRepository = new StubUserRepository();
        RegisterServlet servlet = new RegisterServlet(userRepository);
        DispatcherState dispatcher = new DispatcherState();
        RequestState requestState = new RequestState("/app", null, dispatcher, mapOf(
                "userId", "  TA100  ",
                "name", "  Alice  ",
                "password", "secret1",
                "confirmPassword", "secret1"));
        ResponseState responseState = new ResponseState();

        servlet.doPost(requestState.proxy(), responseState.proxy());

        assertEquals("UTF-8", requestState.characterEncoding);
        assertEquals("TA100", userRepository.lastLookupUserId);
        assertNotNull(userRepository.savedUser);
        assertEquals("TA100", userRepository.savedUser.getUserId());
        assertEquals("Alice", userRepository.savedUser.getName());
        assertEquals("secret1", userRepository.savedUser.getPassword());
        assertEquals("TA", userRepository.savedUser.getRole());
        assertEquals(0, userRepository.savedUser.getActiveJobsCount());
        assertEquals("Account created! You can now sign in with your new TA account.",
                requestState.attributes.get("success"));
        assertTrue(dispatcher.forwarded);
        assertEquals("/jsp/register.jsp", dispatcher.path);
        assertNull(responseState.redirectLocation);
    }

    private static Map<String, String> mapOf(String... values) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }

    private static User user(String userId, String role, String name, String password) {
        User user = new User();
        user.setUserId(userId);
        user.setRole(role);
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    private static final class StubUserRepository extends UserRepository {
        private User existingUser;
        private User savedUser;
        private IOException saveException;
        private String lastLookupUserId;

        @Override
        public User getUserById(String userId) {
            lastLookupUserId = userId;
            return existingUser;
        }

        @Override
        public void saveUser(User user) throws IOException {
            savedUser = user;
            if (saveException != null) {
                throw saveException;
            }
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

        private RequestDispatcher proxy() {
            return (RequestDispatcher) Proxy.newProxyInstance(
                    RequestDispatcher.class.getClassLoader(),
                    new Class<?>[] { RequestDispatcher.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
            if ("forward".equals(method.getName())) {
                forwarded = true;
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

        private SessionState withAttribute(String key, Object value) {
            attributes.put(key, value);
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
