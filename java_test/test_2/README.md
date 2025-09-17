2. 使用 Maven 打包
mvn clean package

参数解释：

clean → 清理之前的编译文件，保证干净打包

package → 编译项目并生成可执行 JAR 文件

3. 打包结果

打包成功后，生成的 JAR 在：

target/<artifactId>-<version>.jar

例如：

target/demo-0.0.1-SNAPSHOT.jar


可以本地测试：

java -jar target/demo-0.0.1-SNAPSHOT.jar


Spring Boot 默认运行在 8080 端口（可以通过 application.properties 或 application.yml 修改）

=======================================================================================

# 上传文件到/home/usr目录
scp ./target/demo-0.0.1-SNAPSHOT.jar  root@219.145.133.19:/home/usr


================================================================================

1️⃣ 停掉旧容器（如果存在）
docker ps             # 查看当前运行容器
docker stop demo-app-container
docker rm demo-app-container

2️⃣ 确认 jar 文件存在
ls -l /home/usr/demo-0.0.1-SNAPSHOT.jar
# 文件大小应该是 47MB 左右，确保 jar 存在且完整

3️⃣ 新建 Dockerfile（可选，如果你已经有 Dockerfile 可跳过）

在 /home/usr 或项目目录里创建 Dockerfile：

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY demo-0.0.1-SNAPSHOT.jar /app/demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo.jar"]


注意 EXPOSE 8080 是容器内部端口，宿主机可以映射成任意可用端口。

4️⃣ 构建镜像
docker build -t demo-app /home/usr


-t demo-app 给镜像取名字

/home/usr 是 Dockerfile 和 jar 所在目录

5️⃣ 运行容器并映射到 5000 端口
docker run -d -p 5000:8080 --name demo-app-container demo-app


-d 后台运行

-p 5000:8080 宿主机 5000 端口映射到容器 8080

--name demo-app-container 容器名字

demo-app 镜像名

6️⃣ 查看日志
docker logs -f demo-app-container


确认 Spring Boot 正常启动

出现 Tomcat started on port(s): 8080 (http) 就说明容器内部服务启动成功