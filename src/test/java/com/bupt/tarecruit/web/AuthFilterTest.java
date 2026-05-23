package com.bupt.tarecruit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;

public class AuthFilterTest {
    private final AuthFilter filter = new AuthFilter();

    @Test
    public void redirectsUnauthenticatedUsersToLogin() throws Exception {
        RecordingResponse response = new RecordingResponse();
        RecordingChain chain = new RecordingChain();

        filter.doFilter(
                request("/app", "/app/mo/home", null),
                response.proxy(),
                chain);

        assertEquals("/app/login", response.redirectLocation);
        assertEquals(-1, response.errorStatus);
        assertFalse(chain.called);
    }

    @Test
    public void rejectsCrossRoleAccessWithForbidden() throws Exception {
        RecordingResponse response = new RecordingResponse();
        RecordingChain chain = new RecordingChain();
        SessionState session = new SessionState()
                .with("userAccount", "TA001")
                .with("userRole", "TA");

        filter.doFilter(
                request("/app", "/app/mo/home", session),
                response.proxy(),
                chain);

        assertNull(response.redirectLocation);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.errorStatus);
        assertEquals("Access denied: MO role required.", response.errorMessage);
        assertFalse(chain.called);
    }

    @Test
    public void allowsMatchingRoleAccess() throws Exception {
        RecordingResponse response = new RecordingResponse();
        RecordingChain chain = new RecordingChain();
        SessionState session = new SessionState()
                .with("userAccount", "MO001")
                .with("userRole", "MO");

        filter.doFilter(
                request("/app", "/app/mo/home", session),
                response.proxy(),
                chain);

        assertNull(response.redirectLocation);
        assertEquals(-1, response.errorStatus);
        assertTrue(chain.called);
    }

    @Test
    public void invalidatesBrokenSessionBeforeRedirectingToLogin() throws Exception {
        RecordingResponse response = new RecordingResponse();
        RecordingChain chain = new RecordingChain();
        SessionState session = new SessionState()
                .with("userRole", "MO");

        filter.doFilter(
                request("/app", "/app/mo/home", session),
                response.proxy(),
                chain);

        assertTrue(session.invalidated);
        assertEquals("/app/login", response.redirectLocation);
        assertFalse(chain.called);
    }

    private HttpServletRequest request(String contextPath, String requestUri, SessionState session) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                new RequestHandler(contextPath, requestUri, session));
    }

    private static final class RecordingChain implements FilterChain {
        private boolean called;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            called = true;
        }
    }

    private static final class RecordingResponse {
        private String redirectLocation;
        private int errorStatus = -1;
        private String errorMessage;

        private HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    HttpServletResponse.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("sendRedirect".equals(name)) {
                redirectLocation = (String) args[0];
                return null;
            }
            if ("sendError".equals(name)) {
                errorStatus = (Integer) args[0];
                errorMessage = args.length > 1 ? (String) args[1] : null;
                return null;
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class RequestHandler implements InvocationHandler {
        private final String contextPath;
        private final String requestUri;
        private final HttpSession session;

        private RequestHandler(String contextPath, String requestUri, SessionState sessionState) {
            this.contextPath = contextPath;
            this.requestUri = requestUri;
            this.session = sessionState == null ? null : sessionState.proxy();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            if ("getContextPath".equals(name)) {
                return contextPath;
            }
            if ("getRequestURI".equals(name)) {
                return requestUri;
            }
            if ("getSession".equals(name)) {
                if (args == null || args.length == 0) {
                    return session;
                }
                boolean create = (Boolean) args[0];
                return create ? session : session;
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class SessionState {
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;

        private SessionState with(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        private HttpSession proxy() {
            return (HttpSession) Proxy.newProxyInstance(
                    HttpSession.class.getClassLoader(),
                    new Class<?>[] { HttpSession.class },
                    this::handle);
        }

        private Object handle(Object proxy, Method method, Object[] args) {
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
