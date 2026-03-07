# 使用 OpenJDK 17 JRE 精简版作为基础镜像
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 包（注意：打包后生成的 JAR 路径为 target/app.jar）
COPY target/app.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 启动命令，激活 docker 配置
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]