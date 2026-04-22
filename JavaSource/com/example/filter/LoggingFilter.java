package com.example.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志 Filter - 通过 web.xml 注册
 * 同时输出到控制台和日志文件
 */
public class LoggingFilter implements Filter {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "logs/access.log";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 确保日志目录存在
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        
        log("[LoggingFilter] 初始化成功");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();
        
        String logMsg = "[请求] " + httpRequest.getMethod() + " " + httpRequest.getRequestURI();
        log(logMsg);
        
        chain.doFilter(request, response);
        
        long duration = System.currentTimeMillis() - startTime;
        String responseMsg = "[响应] 状态: " + response.getContentType() + " (耗时: " + duration + "ms)";
        log(responseMsg);
    }
    
    @Override
    public void destroy() {
        log("[LoggingFilter] 销毁");
    }
    
    /**
     * 写入日志到文件和控制台
     */
    private void log(String message) {
        String timestamp = sdf.format(new Date());
        String logLine = timestamp + " " + message;
        
        // 输出到控制台
        System.out.println(logLine);
        
        // 写入文件
        try {
            File logFile = new File(LOG_FILE);
            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
            writer.println(logLine);
            writer.close();
        } catch (IOException e) {
            System.err.println("写日志失败: " + e.getMessage());
        }
    }
}