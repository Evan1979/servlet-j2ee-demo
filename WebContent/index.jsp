<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>J2EE Demo - 首页</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        h1 { color: #333; }
        .nav { margin: 20px 0; }
        .nav a { margin-right: 20px; }
        .info { background: #f5f5f5; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>🎉 J2EE Servlet Demo</h1>

    <div class="nav">
        <a href="hello">Hello Servlet</a>
        <a href="hello?name=JSTL">Hello + JSTL</a>
        <a href="api/status">API 状态</a>
        <a href="xml/data">XML 数据</a>
        <a href="pages/hello.jsp">JSP 页面</a>
    </div>

    <div class="info">
        <h3>项目信息</h3>
        <ul>
            <li><strong>服务器:</strong> Jetty ${pageContext.servletContext.serverInfo}</li>
            <li><strong>Java 版本:</strong> <%= System.getProperty("java.version") %></li>
            <li><strong>当前时间:</strong> <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss" /></li>
            <li><strong>JSTL 版本:</strong> 1.2.2</li>
        </ul>
    </div>

    <div class="info" style="margin-top: 20px;">
        <h3>JSTL 演示</h3>
        <c:set var="greeting" value="欢迎使用 JSTL!" />
        <p>${greeting}</p>

        <c:forEach var="i" begin="1" end="3">
            <p>循环计数: ${i}</p>
        </c:forEach>

        <c:choose>
            <c:when test="${param.test == 'true'}">
                <p style="color: green;">✅ 测试参数已启用</p>
            </c:when>
            <c:otherwise>
                <p>📌 添加 ?test=true 参数查看条件渲染</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
