<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello JSP - J2EE Demo</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .card { background: #e3f2fd; padding: 20px; border-radius: 10px; margin: 20px 0; }
        .back { margin-top: 30px; }
    </style>
</head>
<body>
    <h1>👋 Hello from JSP!</h1>

    <div class="card">
        <h2>接收的参数</h2>
        <p><strong>name 参数:</strong>
            <c:choose>
                <c:when test="${not empty param.name}">
                    <span style="color: blue;">${param.name}</span>
                </c:when>
                <c:otherwise>
                    <span style="color: gray;">(未提供，使用默认值)</span>
                </c:otherwise>
            </c:choose>
        </p>
    </div>

    <div class="card">
        <h2>JSTL 功能演示</h2>

        <h3>📝 变量设置与输出</h3>
        <c:set var="now" value="<%= new java.util.Date() %>" />
        <p>当前时间: <fmt:formatDate value="${now}" pattern="yyyy年MM月dd日 HH:mm:ss" /></p>

        <h3>🔄 循环</h3>
        <ul>
            <c:forEach var="item" items="${['苹果', '香蕉', '橙子', '葡萄']}">
                <li>水果: ${item}</li>
            </c:forEach>
        </ul>

        <h3>🔢 数字格式化</h3>
        <p>π 格式化: <fmt:formatNumber value="3.1415926" pattern="0.00" /></p>

        <h3>💾 格式化日期</h3>
        <p>完整格式: <fmt:formatDate value="${now}" type="both" /></p>
    </div>

    <div class="back">
        <a href="index.jsp">← 返回首页</a>
    </div>
</body>
</html>
