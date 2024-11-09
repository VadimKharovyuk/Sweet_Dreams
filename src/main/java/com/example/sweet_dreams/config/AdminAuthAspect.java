package com.example.sweet_dreams.config;

import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AdminAuthAspect {

    @Around("@annotation(RequireAdmin)")
    public Object checkAdminAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession session = attributes.getRequest().getSession();

        if (session.getAttribute("adminId") == null) {
            attributes.getResponse().sendRedirect("/admin/login");
            return null;
        }

        return joinPoint.proceed();
    }
}
