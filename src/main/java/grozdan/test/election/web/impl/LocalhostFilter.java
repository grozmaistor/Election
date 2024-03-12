package grozdan.test.election.web.impl;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import java.io.IOException;

public class LocalhostFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (HttpMethod.POST == HttpMethod.valueOf(request.getMethod())) {
            String clientIPAddress = request.getRemoteAddr();
            String serverIPAddress = request.getLocalAddr();
            if (! clientIPAddress.equals(serverIPAddress)) {
                System.out.println("IP address " + clientIPAddress + " is not authorized to make this call!");
                response.reset();
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setHeader("message", "IP address " + clientIPAddress + " is not authorized to make this call!");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}