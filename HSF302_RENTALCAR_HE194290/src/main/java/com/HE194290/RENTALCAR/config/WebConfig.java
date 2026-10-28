package com.HE194290.RENTALCAR.config;

import com.HE194290.RENTALCAR.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Phân quyền theo session: /admin/** chỉ Admin, /customer/** chỉ Customer. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				HttpSession session = request.getSession(false);
				String role = session == null ? null : (String) session.getAttribute("role");
				String path = request.getRequestURI().substring(request.getContextPath().length());
				boolean ok = path.startsWith("/admin") ? Account.ADMIN.equals(role)
						: Account.CUSTOMER.equals(role);
				if (!ok) {
					if ("GET".equals(request.getMethod()) && role == null) {
						String query = request.getQueryString();
						request.getSession(true).setAttribute("nextUrl", path + (query == null ? "" : "?" + query));
					}
					response.sendRedirect(request.getContextPath() + "/login?required");
				}
				return ok;
			}
		}).addPathPatterns("/admin/**", "/customer/**");
	}
}
