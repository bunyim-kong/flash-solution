package com.example.back_end.config;

import com.example.back_end.model.User;
import com.example.back_end.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.http.server.PathContainer.PathSegment;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class ApiAccess implements WebMvcConfigurer {
    public static final String ACCOUNT_ID = "accountId";
    private static final String ACCOUNT = "account";
    private static final Set<String> ADMIN_ONLY = Set.of("users", "admin", "customer-orders", "order-items");
    private static final Set<String> CATALOGUE = Set.of("products", "brands", "categories");
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");

    private final UserRepository users;

    public ApiAccess(UserRepository users) {
        this.users = users;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                boolean read = SAFE_METHODS.contains(request.getMethod());
                // A cross-site form cannot set this header. The API is served through the same origin.
                if (!read && !"1".equals(request.getHeader("X-Lumiere-Request"))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request header is missing");
                }
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute(ACCOUNT_ID) instanceof Long id) {
                    var account = users.findById(id).filter(User::isActive);
                    if (account.isPresent()) request.setAttribute(ACCOUNT, account.get());
                    else session.removeAttribute(ACCOUNT_ID);
                }
                String resource = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication()
                        .elements().stream().filter(PathSegment.class::isInstance).map(PathSegment.class::cast)
                        .skip(1).findFirst().map(PathSegment::valueToMatch).orElse("");
                if (ADMIN_ONLY.contains(resource) || (!read && CATALOGUE.contains(resource))) {
                    if (!isAdmin(requireAccount(request))) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access is required");
                    }
                }
                response.setHeader("Cache-Control", "no-store");
                return true;
            }
        }).addPathPatterns("/api/{resource}", "/api/{resource}/**");
    }

    public static User account(HttpServletRequest request) {
        return (User) request.getAttribute(ACCOUNT);
    }

    public static User requireAccount(HttpServletRequest request) {
        User account = account(request);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to continue");
        }
        return account;
    }

    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
