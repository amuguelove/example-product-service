# Example Product Service

## 开发指南

### 技术栈
- Java 8
- Spring Boot 2.2.2.RELEASE

### 架构和代码结构
本项目代码结构参考 Onion Architecture，参考以下介绍
* [The Onion Architecture : part 1](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/)
* [The Onion Architecture : part 2](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-2/)
* [The Onion Architecture : part 3](https://jeffreypalermo.com/2008/08/the-onion-architecture-part-3/)
* [The Onion Architecture : part 4](https://jeffreypalermo.com/2013/08/onion-architecture-part-4-after-four-years/)

测试策略和结构参考
* [Testing Strategies in a Microservices Architecture](https://martinfowler.com/articles/microservice-testing)

### 本地依赖
通过Docker初始化本地环境和依赖。
```
docker-compose up -d
```

### 本地构建
```
./gradlew clean build
```

### 本地运行
```
./gradlew bootRun
```

### 用Intellij IDEA打开项目
```
./gradlew idea
open example-product-service.ipr
```

### 提交代码
代码提交加入了 `pre-commit` 钩子，每次提交会执行 `./gradlew check`, 详情参考 `gradle/git-hook.gradle`

### 运行Sonar扫描
```
./gradlew sonarqube
```
或者
```
./gradlew -Dsonar.host.url=http://localhost:9000 sonarqube
```
打开 [http://localhost:9000](http://localhost:9000) 检查扫描结果。

### 运行OWASP Dependency Check
直接运行dependency check可能因为下载NVD资源会失败，建议下载后本地运行。
参考[使用OWASP Dependency-Check进行第三方依赖包安全扫描实践](https://www.jianshu.com/p/f1a2f5357d12) 来搭建本地NVD Mirror库。
取消 ./gradle/dependency-check.gradle中cve的本地配置的注释。
运行依赖检查分析：
```
./gradlew dependencyCheckAnalyze
```

