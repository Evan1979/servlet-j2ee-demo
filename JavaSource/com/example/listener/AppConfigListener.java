package com.example.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 应用配置监听器
 * 在应用启动时加载配置文件到 ServletContext
 */
public class AppConfigListener implements ServletContextListener {

    private Properties appConfig;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // 获取 web.xml 中配置的配置文件路径
        String configFile = context.getInitParameter("configFile");
        if (configFile == null || configFile.trim().isEmpty()) {
            configFile = "com/example/app.properties";
        }

        // 加载配置文件
        appConfig = new Properties();
        InputStream is = null;
        try {
            is = context.getResourceAsStream("/WEB-INF/classes/" + configFile);
            if (is == null) {
                // 尝试从 classpath 加载
                is = getClass().getClassLoader().getResourceAsStream(configFile);
            }

            if (is != null) {
                appConfig.load(is);
                System.out.println("✅ 配置文件加载成功: " + configFile);
            } else {
                System.err.println("⚠️ 配置文件未找到: " + configFile);
            }
        } catch (IOException e) {
            System.err.println("❌ 配置文件加载失败: " + e.getMessage());
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException ignored) {}
            }
        }

        // 将配置存入 ServletContext 供后续使用
        if (appConfig != null && !appConfig.isEmpty()) {
            context.setAttribute("app.config", appConfig);

            // 打印配置信息
            System.out.println("\n========== 应用配置 ==========");
            appConfig.forEach((k, v) -> System.out.println("  " + k + " = " + v));
            System.out.println("==============================\n");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("应用关闭，清理配置...");
        appConfig = null;
    }
}
