***第二周周记***

**ai使用和个人贡献说明**

-这次的对比起上周的任务，增加了controller前端接口，exception异常处理器，jwt拦截器
-这部分内容当中，controller让ai写了模板，根据自己需求再从service里面的方法拿出来对应做，期间也有用ai进行纠错
-其他方面，exception，jwt这方面真没怎么学过，只能让ai做了大概，后面根据controller一些域名定义给登录注册页面提供了放行，给静态资源，预检请求放行
-jwt是一种标准，生成token，校验是否带“Bearer ”头再substring 获取后面的内容进行jwt校验
-前端页面方面，主要通过ai实现上周的角色菜单功能，再根据controller的request mapping修改对齐
-这个坑比较恶心的是听ai的给xml配置了/api接口结果没有改controller头的@requestmapping，有了两个/api，登录一直失败
-上一周不少内容都可以迁移过来，mapper，service,pojo建的数据表这些都能带过来
-ai好用是好用但是需要自己对内容了解，否则对接原代码很麻烦
