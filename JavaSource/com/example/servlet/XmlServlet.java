package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 通过 web.xml 注册的 Servlet
 */
public class XmlServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private String configKey;
    
    @Override
    public void init() throws ServletException {
        this.configKey = getInitParameter("configKey");
        System.out.println("[XmlServlet] 初始化, configKey = " + configKey);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><meta charset='UTF-8'><title>XML Servlet</title></head>");
        out.println("<body>");
        out.println("<h1>✅ 这是通过 web.xml 注册的 Servlet!</h1>");
        out.println("<p><strong>Servlet 名称:</strong> XmlServlet</p>");
        out.println("<p><strong>URL 映射:</strong> /xml/*</p>");
        out.println("<p><strong>初始化参数 configKey:</strong> " + configKey + "</p>");
        out.println("<p><strong>加载时机:</strong> load-on-startup=1 (服务器启动时加载)</p>");
        out.println("<hr>");
        out.println("<p><a href='/hello'>测试 HelloServlet (@WebServlet)</a></p>");
        out.println("<p><a href='/xml/test'>测试 XmlServlet (web.xml)</a></p>");
        out.println("</body></html>");
    }
}