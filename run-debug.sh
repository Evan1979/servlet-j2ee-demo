cd ~/.openclaw/workspace/servlet-j2ee

# 设置 Java 环境
export JAVA_HOME=/opt/dev-dependecy/openjdk8
export PATH=$JAVA_HOME/bin:/opt/dev-dependecy/maven/bin:$PATH

# 复制依赖
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency

# 启动 (带调试)
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -cp "target/classes:target/dependency/*" \
     com.example.StartJettyDebug --port 8080