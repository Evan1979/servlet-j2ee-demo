# Servlet J2EE Demo - Tomcat 版本

基于 Tomcat 9 嵌入式的 J2EE Servlet 项目，采用 Eclipse 传统目录结构。

## 目录结构

```
servlet-j2ee-tomcat/
├── JavaSource/              # Java 源代码
│   └── com/example/
│       ├── listener/        # 监听器 (配置加载)
│       ├── filter/          # 过滤器
│       ├── servlet/         # Servlet
│       ├── app.properties    # 配置文件
│       └── StartTomcatDebug.java
├── WebContent/              # Web 资源
│   ├── WEB-INF/
│   │   └── web.xml
│   ├── index.jsp            # JSP 首页 (含 JSTL)
│   └── pages/
│       └── hello.jsp        # JSP 演示页
└── pom.xml
```

## 技术栈

- **JDK**: 1.8
- **Servlet API**: 4.0.1
- **JSP**: 2.3
- **JSTL**: 1.2.2
- **Tomcat**: 9.0.80 (嵌入式)

## 快速开始

### 编译项目

```bash
mvn clean compile
```

### 运行 Tomcat

```bash
# 设置 classpath
export CP="target/classes:target/dependency/*"

# 编译并运行
mvn compile exec:java -Dexec.mainClass="com.example.StartTomcatDebug"

# 或手动运行
java -cp "$CP" com.example.StartTomcatDebug --port 8080
```

### 调试模式

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -cp "$CP" \
     com.example.StartTomcatDebug --port 8080
```

## 可用端点

| 端点 | 说明 |
|------|------|
| `/index.jsp` | JSP 首页 (含 JSTL 演示) |
| `/hello?name=xxx` | Hello Servlet |
| `/api/status` | API 状态 |
| `/xml/*` | XML Servlet |
| `/pages/hello.jsp` | JSP 功能演示 |

## 配置文件

配置文件位于 `JavaSource/com/example/app.properties`，通过 `web.xml` 中的 `AppConfigListener` 在启动时加载。

```xml
<context-param>
    <param-name>configFile</param-name>
    <param-value>com/example/app.properties</param-value>
</context-param>
<listener>
    <listener-class>com.example.listener.AppConfigListener</listener-class>
</listener>
```
