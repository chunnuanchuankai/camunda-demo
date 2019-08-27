# 一、springboot整合Camunda
- 1、创建一个空maven项目
- 2、引入camunda依赖包
```xml
<dependencies>
    <dependency>
        <groupId>org.camunda.bpm.springboot</groupId>
        <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>
        <version>3.3.1</version>
    </dependency>
    <!-- 这里使用号称光一般快速的数据源`Hikari`，也是`SpringBoot 2.x`后默认使用的数据源 -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>3.3.1</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```
- 3、配置数据源
```yaml
server:
  port: 8080
  tomcat:
    uri-encoding: UTF-8

spring:
  application:
    name: camunda-demo
  datasource:
    username: root
    password: 761341
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/camunda-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC
```
- 4、启动项目
> 可以见到日志中前两行hikariPool启动，后面执行camunda内置sql创建表
```text
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
ENGINE-03016 Performing database operation 'create' on component 'engine' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.engine.sql'
ENGINE-03016 Performing database operation 'create' on component 'history' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.history.sql'
ENGINE-03016 Performing database operation 'create' on component 'identity' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.identity.sql'
ENGINE-03016 Performing database operation 'create' on component 'case.engine' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.case.engine.sql'
ENGINE-03016 Performing database operation 'create' on component 'case.history' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.case.history.sql'
ENGINE-03016 Performing database operation 'create' on component 'decision.engine' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.decision.engine.sql'
ENGINE-03016 Performing database operation 'create' on component 'decision.history' with resource 'org/camunda/bpm/engine/db/create/activiti.mysql.create.decision.history.sql'
```
- 5、继续往`application.yml`中添加配置，创建root账号
```yaml
camunda.bpm:
  admin-user:
    id: admin
    password: admin
    firstName: admin
  filter:
    create: All tasks
```

# 绘制bpm流程图
- 1、下载
下载modeler流程图绘制工具：https://camunda.com/download/modeler/