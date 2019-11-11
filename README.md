## 简介

**JT／T808-2011   java 实现体系**   

主要技术 Spring Boot 2.2.1.RELEASE,  Spring 5.2.1.RELEASE ，Netty  4.1.43Final  

### 目的

实现 doc 中的 JT-808 协议，本项目主要实现的是 《驾图终端808通信协议标准_V1.00.docx》

doc 中还包含 TCP/UDP 测试工具

### 项目入口

cn.sanleny.jt808.server.Application

### TCP 服务

对外暴露端口，默认 8864 

cn.sanleny.jt808.server.framework.server.Jt808TcpServer

### 核心模块

cn.sanleny.jt808.server.framework

该模块主要实现了 TCP的连接管理、 JT808 协议消息的解析，以及对终端的响应等功能

### 协议处理模块

`cn.sanleny.jt808.server.protocol`

该模块主要是对各种 GPRS 协议的解析，以及相应的业务处理

对于这些具体的协议处理类只需要继承抽象类 `cn.sanleny.jt808.server.framework.handler.AbstractProtocolProcess` ，实现其中的 `resolve` 和 `process` 方法即可。

> tip：可以在 `resolve` 方法中解析消息数据数组，也可以直接返回一个体类，交由 CGLIB 的动态代理类 `cn.sanleny.jt808.server.framework.aop.ResolveMethodInterceptor`   来处理，前提是你的实体类得符合解析的规则：需要在对应的实体类上增加注解 `@Jt808Resolver` ，类中的字段上增加注解  `@Jt808Field`

### 其它

目前只实现了一部分的功能，后续会继续完善的...

如果认为此项目对你有帮助，请在右上角轻点下 start 让作者更有动力的持续完善哦 ...

## 测试数据

记录了一些相关的测试数据，方便测试，以下都是16进制编码

### 终端注册

7E0100002D60081900027805BC002C001E34343033306B6172746F7200000000000000000000000000004D38393030303201D4C1423939383838AE7E

### 终端鉴权

7E0102001A60081900027805C1313233367D027D0281000006600819000278000305BF00313233367D029D7E

### 链路心跳
7E0002000060081900027803A5AF7E

### 位置信息 

7E0200004F502820408000481F000000000000000301584CC106CA88610043000001031911010912460104000016EA020209B7E3273147314A433534343452373235323336382C2C1791BC823637363135303000000000EEEEEEEEEEA17E

### 数据上行透传
7E0900003760081900027801E2F0191107100230000000000000000000000002AD007D01000000000000CF55005700570B00570000002A002C00000C02005705820103E8012C7E



## 致谢

其中的一些实现参考了一些资料，特在此处表示感谢

作者 hylexus  的项目 **[jt-808-protocol](https://github.com/hylexus/jt-808-protocol)**