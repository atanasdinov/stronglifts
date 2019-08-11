package sl.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sl.configuration.SecurityConfig;
import sl.model.User;
import sl.service.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


/**
 * This class is inheriting {@link Filter} in order to call each time a route is requested.
 * Its purpose is to make the Authentication logic for the application.
 */
public class AuthenticationFilter implements Filter {

    private UserService userService;

    /**
     * Assign a value to the property.
     *
     * @param userService contain the autowired value from {@link SecurityConfig}.
     */
    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * {@inheritDoc}
     * The logic of Authentication is made here. The cookie is gotten from the request in order to check if user contains such token.
     * If so the role in the database is set to the SecurityContext. If not the logger is notified.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String token = request.getParameter("token");

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);

        Optional<User> user = userService.verifyToken(token);

        if (user.isPresent())
            context.setAuthentication(new UsernamePasswordAuthenticationToken(user.get().getUsername(), user.get().getPassword()));
        else {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
            context.getAuthentication().setAuthenticated(false);
        }

        if (!request.getMethod().equalsIgnoreCase("OPTIONS"))
            filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

