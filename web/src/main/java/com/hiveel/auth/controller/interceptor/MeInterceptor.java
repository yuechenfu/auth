package com.hiveel.auth.controller.interceptor;

import com.google.gson.Gson;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.MemcachedUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MeInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Account loginAccount = (Account) request.getAttribute("loginAccount");
        if (loginAccount == null) {
            setResponse(response);
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.getWriter().print(new Gson().toJson(Rest.createFail(BasicRestCode.UNAUTHORIZED)));
    }

}
