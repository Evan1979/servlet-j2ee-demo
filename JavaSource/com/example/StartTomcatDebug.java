package com.example;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

/**
 * 启动 Tomcat 嵌入式服务器
 * 支持: web.xml 配置 + Listener + JSP + JSTL
 *
 * 用法:
 *   java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
 *        -cp "target/classes:target/dependency/*" \
 *        com.example.StartTomcatDebug --port 8080
 */
public class StartTomcatDebug {

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

        // 创建 Tomcat 实例
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(httpPort);
        tomcat.getConnector(); // 触发连接器初始化

        // 设置工作目录
        File baseDir = new File("target/tomcat");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        tomcat.setBaseDir(baseDir.getAbsolutePath());

        // 创建 WebContent 目录上下文
        File webContentDir = new File("WebContent").getAbsoluteFile();
        Context context = tomcat.addWebapp("", webContentDir.getAbsolutePath());

        // 注册 web.xml
        context.setConfigFile(new java.net.URL("file:" + webContentDir.getAbsolutePath() + "/WEB-INF/web.xml"));

        // 启动 Tomcat
        tomcat.start();

        System.out.println("\n✅ Tomcat 已启动！按 Ctrl+C 停止\n");
        System.out.println("可用端点:");
        System.out.println("   📍 /index.jsp (JSP 首页)");
        System.out.println("   📍 /hello?name=xxx (Hello Servlet)");
        System.out.println("   📍 /api/status (API 状态)");
        System.out.println("   📍 /xml/data (XML 数据)");
        System.out.println("   📍 /pages/hello.jsp (JSP 演示)");
        System.out.println("\n   访问地址: http://localhost:" + httpPort);
        System.out.println("   Web.xml: " + webContentDir.getAbsolutePath() + "/WEB-INF/web.xml");

        tomcat.getServer().await();
    }

    private static void printBanner(int httpPort, int debugPort) {
        System.out.println("===========================================");
        System.out.println("  Tomcat 启动");
        System.out.println("  HTTP 端口: " + httpPort);
        System.out.println("  调试端口: " + debugPort);
        System.out.println("===========================================");
    }
}
