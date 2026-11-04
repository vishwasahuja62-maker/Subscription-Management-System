package com.subscription.servlet;

import com.subscription.model.User;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getServletPath();

        if (isPublicPath(path) || isLoginRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        Object loggedUser = session == null ? null : session.getAttribute("loggedUser");
        if (loggedUser instanceof User && "Admin".equals(((User) loggedUser).getRole())) {
            chain.doFilter(request, response);
            return;
        }

        if (ServletSupport.wantsJson(httpRequest)) {
            ServletSupport.writeError(
                    httpRequest, httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in to continue.");
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void destroy() {}

    private boolean isPublicPath(String path) {
        return "/login.jsp".equals(path)
                || "/LogoutServlet".equals(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || "/favicon.ico".equals(path);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "/LoginServlet".equals(request.getServletPath())
                && !"dashboard".equals(request.getParameter("action"));
    }
}
