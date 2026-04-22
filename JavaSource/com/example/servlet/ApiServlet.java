package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "ApiServlet", urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            out.print("{\"status\":\"ok\",\"message\":\"J2EE Servlet API\",\"version\":\"1.0\"}");
        } else if (pathInfo.equals("/status")) {
            out.print("{\"status\":\"running\",\"server\":\"Jetty\",\"jdk\":\"1.8\",\"time\":\"" + sdf.format(new Date()) + "\"}");
        } else if (pathInfo.equals("/time")) {
            out.print("{\"timestamp\":" + System.currentTimeMillis() + ",\"datetime\":\"" + sdf.format(new Date()) + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Not Found\"}");
        }
    }
}