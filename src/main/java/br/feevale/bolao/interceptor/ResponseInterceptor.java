package br.feevale.bolao.interceptor;

import br.feevale.bolao.model.Success;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

//        String json = "";
        if (!request.getMethod().equals("OPTIONS")) {
            String json = new ObjectMapper().writeValueAsString(new Success());
            response.getWriter().write(json);
            response.flushBuffer();
        }
//        response.getOutputStream().write(json.getBytes());

//        super.afterCompletion(request, response, handler, ex);
    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
////        HttpServletResponse newResponse;
////        PrintWriter print = response.getWriter();
//        String json = "";
//        if (!request.getMethod().equals("OPTIONS")) {
//            json = new ObjectMapper().writeValueAsString(new Success());
//            response.getWriter().write(json);
//            response.flushBuffer();
//        }
//        response.getOutputStream().write(json.getBytes());
////        newResponse.getWriter().write(print.toString());
//        super.postHandle(request, response, handler, modelAndView);
//    }

}
