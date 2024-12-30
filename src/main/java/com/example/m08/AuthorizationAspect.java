package com.example.m08;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;

@Aspect
@Component
public class AuthorizationAspect {

    @Around("@annotation(requiredRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiredRole requiredRole) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            return "redirect:/login";
        }

        // Check for wildcard role
        String[] requiredRoles = requiredRole.value();
        if (Arrays.asList(requiredRoles).contains("*")) {
            if (session.getAttribute("admin") != null || session.getAttribute("pelanggan") != null) {
                return joinPoint.proceed();
            }
            return "redirect:/login";
        }

        // Get user role
        String userRole = null;
        if (session.getAttribute("admin") != null) {
            userRole = "admin";
        } else if (session.getAttribute("pelanggan") != null) {
            userRole = "user";
        }

        if (userRole == null) {
            return "redirect:/login";
        }

        // Check if user has required role
        if (Arrays.asList(requiredRoles).contains(userRole)) {
            return joinPoint.proceed();
        }

        // Redirect based on role
        if (userRole.equals("admin")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/userdashboard";
        }
    }
}