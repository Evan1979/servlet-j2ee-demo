# 企业级 Java 开发实战：受限环境下的 Jetty 调试方案

> 作者：MEvan & OpenClaw AI  
> 日期：2026-04-06  
> 适用环境：JDK 1.8 + Maven + VS Code

---

## 背景

在企业开发环境中，常面临以下限制：
- **禁用 Tomcat**：安全扫描或合规要求
- **禁用 IDEA**：需要使用轻量级 IDE
- **网络受限**：无法访问内部仓库以外的资源

本文介绍如何使用 **Jetty** (嵌入式服务器) + **VS Code** 实现 Java Web 应用的本地调试，支持：
- ✅ @WebServlet 注解
- ✅ web.xml 配置
- ✅ @WebFilter 注解 + web.xml Filter
- ✅ 配置文件加载
- ✅ 断点调试

---

## 一、环境准备

### 1.1 软件要求

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | OpenJDK 或 Oracle JDK |
| Maven | 3.6+ | 构建工具 |
| VS Code | 1.70+ | 代码编辑器 |
| VS Code 插件 | - | Extension Pack for Java |

### 1.2 项目结构

```
servlet-j2ee/
├── pom.xml                              # Maven 配置
├── README.md                            # 项目文档
├── .vscode/
│   ├── launch.json                      # 调试配置
│   └── tasks.json                       # 任务配置
├── src/main/
│   ├── java/com/example/
│   │   ├── StartJettyDebug.java         # 嵌入式启动器 (自动扫描)
│   │   ├── servlet/
│   │   │   ├── HelloServlet.java        # @WebServlet 注解示例
│   │   │   ├── ApiServlet.java          # REST API
│   │   │   └── XmlServlet.java          # web.xml 配置示例
│   │   └── filter/
│   │       └── LoggingFilter.java       # Filter 示例
│   ├── resources/
│   │   └── app.properties               # 配置文件
│   └── webapp/
│       ├── index.html
│       └── WEB-INF/
│           └── web.xml                   # XML 配置
└── target/                              # 编译输出
    └── dependency/                       # 运行时依赖
```

---

## 二、快速开始

### 2.1 克隆或下载项目

```bash
cd ~/workspace
git clone <your-repo-url>
cd servlet-j2ee
```

### 2.2 编译项目

```bash
export JAVA_HOME=/opt/dev-dependecy/openjdk8
export PATH=$JAVA_HOME/bin:/opt/dev-dependecy/maven/bin:$PATH

mvn clean compile dependency:copy-dependencies -DoutputDirectory=target/dependency
```

### 2.3 启动 Jetty (调试模式)

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -cp "target/classes:target/dependency/*" \
     com.example.StartJettyDebug --port 8080
```

### 2.4 VS Code 调试

1. 按 `F5` → 选择 **"附加到 Jetty (5005)"**
2. 在代码设置断点
3. 浏览器访问 `http://localhost:8080/hello`

---

## 三、VS Code 配置 (推荐)

### 3.1 Tasks 任务

按 `Ctrl+Shift+P` → `Tasks: Run Task` → 选择任务：

| 任务 | 说明 |
|------|------|
| 启动 Jetty (调试模式) | 带 5005 调试端口 |
| 启动 Jetty (普通模式) | 不带调试 |
| 编译项目 | mvn compile |
| 清理并编译 | mvn clean compile |

### 3.2 调试配置

```json
// .vscode/launch.json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "附加到 Jetty (5005)",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005
        },
        {
            "type": "java",
            "name": "启动 Jetty (带调试)",
            "request": "launch",
            "mainClass": "com.example.StartJettyDebug",
            "args": "--port 8080",
            "vmArgs": "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
        }
    ]
}
```

---

## 四、核心配置

### 4.1 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>servlet-j2ee</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <jetty.version>9.4.51.v20230217</jetty.version>
    </properties>

    <dependencies>
        <!-- Servlet API -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
        </dependency>
        
        <!-- 嵌入式 Jetty -->
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>${jetty.version}</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-servlet</artifactId>
            <version>${jetty.version}</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-webapp</artifactId>
            <version>${jetty.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <debug>true</debug>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4.2 嵌入式启动器 (自动扫描)

`StartJettyDebug.java` 会自动扫描：
- `@WebServlet` 注解的 Servlet
- `@WebFilter` 注解的 Filter
- `web.xml` 中配置的 Servlet 和 Filter
- 配置文件 (`app.properties`)

### 4.3 web.xml 配置示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="4.0">
    
    <!-- Filter 配置 -->
    <filter>
        <filter-name>LoggingFilter</filter-name>
        <filter-class>com.example.filter.LoggingFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>LoggingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <!-- Servlet 配置 -->
    <servlet>
        <servlet-name>XmlServlet</servlet-name>
        <servlet-class>com.example.servlet.XmlServlet</servlet-class>
        <init-param>
            <param-name>configKey</param-name>
            <param-value>configValue</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>XmlServlet</servlet-name>
        <url-pattern>/xml/*</url-pattern>
    </servlet-mapping>
    
</web-app>
```

### 4.4 注解示例

```java
// @WebServlet 注解
@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet { }

// @WebFilter 注解
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter { }
```

---

## 五、日志配置

### 5.1 日志输出

启动后日志实时显示在终端，同时写入文件：
- **终端输出**：实时查看请求/响应
- **日志文件**：`logs/access.log`

### 5.2 自定义日志

修改 `LoggingFilter` 中的配置：
```java
private static final String LOG_FILE = "logs/access.log";
```

---

## 六、安全与合规

### 6.1 端口安全

当前配置**仅绑定 localhost** (127.0.0.1)，不会暴露到公网。

### 6.2 许可证合规

| 服务器 | 许可证 | 商业使用 |
|--------|--------|----------|
| **Jetty** | Apache 2.0 + EPL 1.0 | ✅ 免费 |
| Tomcat | Apache 2.0 | ✅ 免费 |

---

## 七、常见问题

### Q1: 端口被占用

```bash
lsof -i :8080
kill -9 <PID>
```

### Q2: 调试端口无法连接

确保启动命令包含调试参数：
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

### Q3: 类找不到

```bash
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
```

---

## 八、与 Spring Boot 对比

| 特性 | 纯 Servlet + Jetty | Spring Boot |
|------|---------------------|-------------|
| 依赖大小 | ~5 MB | ~30 MB+ |
| 启动速度 | 快 | 较慢 |
| 配置复杂度 | 低 | 中等 |
| 调试支持 | ✅ 完全支持 | ✅ 完全支持 |

---

## 九、贡献指南

欢迎提交 Issue 和 Pull Request！

---

## 参考资料

- [Jetty 官方文档](https://www.eclipse.org/jetty/documentation/)
- [VS Code Java 调试](https://code.visualstudio.com/docs/java/java-debugging)

---

**项目地址**: https://github.com/your-username/servlet-j2ee-demo
