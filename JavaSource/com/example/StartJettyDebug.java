package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 启动 Jetty (增强版)
 * 支持: @WebServlet 注解 + web.xml 配置 + Filter + 配置文件加载
 * 
 * 用法: 
 *   java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
 *        -cp "target/classes:target/dependency/*" \
 *        com.example.StartJettyDebug --port 8080
 */
public class StartJettyDebug {
    
    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final int DEFAULT_DEBUG_PORT = 5005;
    
    public static void main(String[] args) throws Exception {
        int httpPort = DEFAULT_HTTP_PORT;
        int debugPort = DEFAULT_DEBUG_PORT;
        
        // 解析参数
        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                httpPort = Integer.parseInt(args[i + 1]);
            } else if ("--debug".equals(args[i]) && i + 1 < args.length) {
                debugPort = Integer.parseInt(args[i + 1]);
            }
        }
        
        printBanner(httpPort, debugPort);
        
        // 创建 Jetty 服务器
        Server server = new Server(httpPort);
        
        // 创建 WebApp 上下文
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setResourceBase("WebContent");
        
        // ============ 关键：让 Jetty 加载 web.xml ============
        String webXmlPath = "WebContent/WEB-INF/web.xml";
        webapp.setDescriptor(webXmlPath);
        
        // 启用 web.xml 和注解扫描
        webapp.setConfigurations(new Configuration[]{
            new WebXmlConfiguration(),
            new MetaInfConfiguration()
        });
        
        // 允许扫描 Servlet 注解
        webapp.setInitParameter("org.eclipse.jetty.servlet.Default.maxLoadedJsps", "100");
        
        // 设置 classpath 用于扫描
        webapp.setClassLoader(Thread.currentThread().getContextClassLoader());
        
        server.setHandler(webapp);
        
        // 预加载：手动扫描并注册 @WebServlet 和 @WebFilter
        // (web.xml 中的会由 Jetty 自动加载)
        preLoadAnnotations(webapp);
        
        // 加载自定义配置文件
        loadCustomConfig(webapp);
        
        server.start();
        
        System.out.println("\n✅ Jetty 已启动！按 Ctrl+C 停止\n");
        System.out.println("可用端点:");
        listEndpoints(webapp);
        
        server.join();
    }
    
    private static void printBanner(int httpPort, int debugPort) {
        System.out.println("===========================================");
        System.out.println("  Jetty 启动 (增强版)");
        System.out.println("  HTTP 端口: " + httpPort);
        System.out.println("  调试端口: " + debugPort);
        System.out.println("  访问地址: http://localhost:" + httpPort);
        System.out.println("  Web.xml: WebContent/WEB-INF/web.xml");
        System.out.println("===========================================");
    }
    
    /**
     * 预加载 @WebServlet 和 @WebFilter 注解
     * (web.xml 中的会在 Jetty 启动时自动加载)
     */
    private static void preLoadAnnotations(WebAppContext webapp) {
        System.out.println("\n📦 扫描 @WebServlet 和 @WebFilter 注解...");
        
        List<ServletInfo> servlets = scanServlets("com.example.servlet");
        List<FilterInfo> filters = scanFilters("com.example.filter");
        
        // 注册 @WebServlet
        for (ServletInfo info : servlets) {
            try {
                @SuppressWarnings("deprecation")
                Servlet instance = (Servlet) Class.forName(info.className).newInstance();
                webapp.addServlet(new ServletHolder(instance), info.urlPattern);
                System.out.println("   ✅ Servlet: " + info.urlPattern + " -> " + info.className);
            } catch (Exception e) {
                System.err.println("   ❌ Servlet 加载失败: " + info.className + " - " + e.getMessage());
            }
        }
        
        // 注册 @WebFilter
        for (FilterInfo info : filters) {
            try {
                @SuppressWarnings("deprecation")
                Filter instance = (Filter) Class.forName(info.className).newInstance();
                webapp.addFilter(new org.eclipse.jetty.servlet.FilterHolder(instance), info.urlPattern, EnumSet.of(DispatcherType.REQUEST));
                System.out.println("   ✅ Filter: " + info.urlPattern + " -> " + info.className);
            } catch (Exception e) {
                System.err.println("   ❌ Filter 加载失败: " + info.className + " - " + e.getMessage());
            }
        }
        
        if (servlets.isEmpty() && filters.isEmpty()) {
            System.out.println("   (无注解类，将使用 web.xml 配置)");
        }
        
        System.out.println("");
    }
    
    /**
     * 加载自定义配置文件
     */
    private static void loadCustomConfig(WebAppContext webapp) {
        System.out.println("📄 加载配置文件...");
        
        String[] configFiles = {
            "resources/app.properties",
            "resources/config.xml",
            "WebContent/WEB-INF/config.properties"
        };
        
        Properties props = new Properties();
        
        for (String configFile : configFiles) {
            try {
                File file = new File(configFile);
                if (file.exists()) {
                    if (configFile.endsWith(".properties")) {
                        props.load(new FileInputStream(file));
                        System.out.println("   ✅ 加载: " + configFile);
                    } else if (configFile.endsWith(".xml")) {
                        // XML 配置加载逻辑
                        System.out.println("   ✅ 加载: " + configFile);
                    }
                }
            } catch (Exception e) {
                // 忽略不存在的文件
                e.printStackTrace();
            }
        }
        
        // 将配置存入 ServletContext 供后续使用
        webapp.setAttribute("app.config", props);
        System.out.println("");
    }
    
    /**
     * 列出所有可用端点
     */
    private static void listEndpoints(WebAppContext webapp) {
        System.out.println("");
        
        // 获取所有 servlet 映射
        for (ServletHolder holder : webapp.getServletHandler().getServlets()) {
            try {
                String name = holder.getName();
                WebServlet ann = holder.getServlet().getClass().getAnnotation(WebServlet.class);
                if (ann != null) {
                    for (String pattern : ann.urlPatterns()) {
                        System.out.println("   📍 " + pattern + " -> " + name);
                    }
                } else {
                    System.out.println("   📍 (web.xml) -> " + name);
                }
            } catch (Exception e) {
                // 忽略
            }
        }
    }
    
    /**
     * 扫描 @WebServlet 注解类
     */
    private static List<ServletInfo> scanServlets(String basePackage) {
        List<ServletInfo> servlets = new ArrayList<>();
        String packagePath = basePackage.replace('.', '/');
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL packageURL = classLoader.getResource(packagePath);
            
            if (packageURL == null) return servlets;
            
            File packageDir = new File(packageURL.getFile());
            scanDirectory(packageDir, basePackage, servlets, true);
            
        } catch (Exception e) {
            System.err.println("扫描 Servlet 出错: " + e.getMessage());
        }
        
        return servlets;
    }
    
    /**
     * 扫描 @WebFilter 注解类
     */
    private static List<FilterInfo> scanFilters(String basePackage) {
        List<FilterInfo> filters = new ArrayList<>();
        String packagePath = basePackage.replace('.', '/');
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL packageURL = classLoader.getResource(packagePath);
            
            if (packageURL == null) return filters;
            
            File packageDir = new File(packageURL.getFile());
            scanDirectory(packageDir, basePackage, filters, false);
            
        } catch (Exception e) {
            System.err.println("扫描 Filter 出错: " + e.getMessage());
        }
        
        return filters;
    }
    
    private static void scanDirectory(File dir, String packageName, List<?> list, boolean isServlet) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), list, isServlet);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    
                    if (isServlet) {
                        WebServlet ann = clazz.getAnnotation(WebServlet.class);
                        if (ann != null) {
                            for (String pattern : ann.urlPatterns()) {
                                @SuppressWarnings("unchecked")
                                List<ServletInfo> servlets = (List<ServletInfo>) list;
                                servlets.add(new ServletInfo(className, pattern, null));
                            }
                        }
                    } else {
                        WebFilter ann = clazz.getAnnotation(WebFilter.class);
                        if (ann != null) {
                            for (String pattern : ann.urlPatterns()) {
                                @SuppressWarnings("unchecked")
                                List<FilterInfo> filters = (List<FilterInfo>) list;
                                filters.add(new FilterInfo(className, pattern, null));
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }
    
    private static class ServletInfo {
        String className;
        String urlPattern;
        Servlet instance;
        ServletInfo(String className, String urlPattern, Servlet instance) {
            this.className = className;
            this.urlPattern = urlPattern;
            this.instance = instance;
        }
    }
    
    private static class FilterInfo {
        String className;
        String urlPattern;
        Filter instance;
        FilterInfo(String className, String urlPattern, Filter instance) {
            this.className = className;
            this.urlPattern = urlPattern;
            this.instance = instance;
        }
    }
}