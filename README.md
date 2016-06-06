# Motanx

# Overview
Motanx is a remote procedure call(RPC) framework for rapid development of high performance distributed services.

# Features
- Create distributed services without writing extra code.
- Provides cluster support and integrate with popular service discovery services like [Consul][consul] or [Zookeeper][zookeeper]. 
- Supports advanced scheduling features like weighted load-balance, scheduling cross IDCs, etc.
- Optimization for high load scenarios, provides high availability in production environment.

# Quick Start

The quick start gives very basic example of running client and server on the same machine. For the detailed information about using and developing Motan, please jump to [Documents](#documents).

> The minimum requirements to run the quick start are: 
>  * JDK 1.7 or above
>  * A java-based project management software like [Maven][maven] or [Gradle][gradle]

> 1.spring schema命名空间修改为http://fn.ly.com/schema/motanx
> 2.ref invoke handler对应的Object的方法调用bug处理
> 3.修改spi扩展，将motan协议修改为我们自己的motanx协议
> 4.去除性能测试模块motan-benchmark, 只保留demo模块
> 5.修改包名称

1. Add dependencies to pom.

   ```xml
    <dependency>
        <groupId>com.ly.fn.motanx</groupId>
        <artifactId>motanx-core</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.ly.fn.motanx</groupId>
        <artifactId>motanx-transport-netty</artifactId>
        <version>0.1.0</version>
    </dependency>
    
    <!-- dependencies blow were only needed for spring-based features -->
    <dependency>
        <groupId>com.ly.fn.motanx</groupId>
        <artifactId>motanx-spring</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.2.4.RELEASE</version>
    </dependency>
   ```

2. Create an interface for both service provider and consumer.

    `src/main/java/quickstart/FooService.java`  

    ```java
    package quickstart;

    public interface FooService {
        public String hello(String name);
    }
    ```

3. Write an implementation, create and start RPC Server.
    
    `src/main/java/quickstart/FooServiceImpl.java`  
    
    ```java
    package quickstart;

    public class FooServiceImpl implements FooService {

    	public String hello(String name) {
            System.out.println(name + " invoked rpc service");
            return "hello " + name;
    	}
    }
    ```

    `src/main/resources/motan_server.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xmlns:motanx="http://fn.ly.com/schema/motanx"
    	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
       http://fn.ly.com/schema/motanx http://fn.ly.com/schema/motanx.xsd">

        <!-- service implemention bean -->
        <bean id="serviceImpl" class="quickstart.FooServiceImpl" />
        <!-- exporting service by motan -->
        <motanx:service interface="quickstart.FooService" ref="serviceImpl" export="8002" />
    </beans>
    ```
    
    `src/main/java/quickstart/Server.java`
    
    ```java
    package quickstart;
    
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public class Server {
    
        public static void main(String[] args) throws InterruptedException {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:motan_server.xml");
            System.out.println("server start...");
        }
    }
    ```
    
    Execute main function in Server will start a motan server listening on port 8002.

4. Create and start RPC Client.

    `src/main/resources/motan_client.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:motanx="http://fn.ly.com/schema/motanx"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
       http://fn.ly.com/schema/motanx http://fn.ly.com/schema/motanx.xsd">

        <!-- reference to the remote service -->
        <motanx:referer id="remoteService" interface="quickstart.FooService" directUrl="localhost:8002"/>
    </beans>
    ```

    `src/main/java/quickstart/Client.java`

    ```java
    package quickstart;

    import org.springframework.context.ApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;


    public class Client {
    
        public static void main(String[] args) throws InterruptedException {
            ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:motan_client.xml");
            FooService service = (FooService) ctx.getBean("remoteService");
            System.out.println(service.hello("motan"));
        }
    }
    ```
    
    Execute main function in Client will invoke the remote service and print response.
    
# License

Motanx is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

[maven]:https://maven.apache.org
[gradle]:http://gradle.org
[consul]:http://www.consul.io
[zookeeper]:http://zookeeper.apache.org


