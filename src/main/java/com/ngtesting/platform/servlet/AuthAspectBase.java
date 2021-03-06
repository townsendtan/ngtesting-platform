package com.ngtesting.platform.servlet;

import com.ngtesting.platform.model.TstUser;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public abstract class AuthAspectBase {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Map<String, Object> getParam(JoinPoint joinPoint){
        Map<String, Object> map = new HashMap<>();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        Parameter[] params =  method.getParameters();

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];

            if (p.getType().getSimpleName().equals("HttpServletRequest")) {
                map.put("request", joinPoint.getArgs()[i]);
            }
            if (p.getType().getSimpleName().equals("JSONObject")) {
                map.put("json", joinPoint.getArgs()[i]);
            }
        }

        return map;
    }

    protected void checkAndLog(String perm, MethodSignature classAndMethod, TstUser user, Integer orgId, String opt){
        log(perm, classAndMethod, user, orgId, opt);
        SecurityUtils.getSubject().checkPermission(perm);
    }

    protected void log(String perm, MethodSignature signature, TstUser user, Integer orgId, String opt){
        String classAndMethod = signature.getMethod().getDeclaringClass().getSimpleName()
                + "." + signature.getMethod().getName();

        logger.info("AuthAspect Require  = " + perm);
//        logger.info("AuthAspect Result  = " + success);

        logger.info("                    - " + classAndMethod);

        String info = "                      " + "user: " + user.getId() + ", " + user.getEmail();

        if (orgId != null) {
            info += ", orgId = " + orgId;
        }
        if (opt != null) {
            info += ", opt: " + opt;
        }

        logger.info(info);
    }
}