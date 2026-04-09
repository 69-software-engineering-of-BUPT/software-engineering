package com.bupt.tarecruit.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

class AuthFilterTest {

    private final AuthFilter filter = new AuthFilter();

    @Test
    void redirectsUnauthenticatedTaRequestToLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/ta/home");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void blocksCrossRoleAccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getRequestURI()).thenReturn("/mo/home");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("TA");

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void allowsMatchingRoleAccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getRequestURI()).thenReturn("/ta/home");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userRole")).thenReturn("TA");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}
