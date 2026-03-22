# 宿舍维修系统 - 第 01 周进度

## 功能完成
- 基础架构：Maven + MyBatis 项目搭建
- 数据层：User/StudentDorm/RepairTable 实体与 Mapper 实现
- 业务层：对应 Service 业务逻辑封装
- 工具层：MyBatisUtil（数据库连接）、RegEx（校验工具）
- 交互层：多角色菜单（Admin/Repair/Student）与 Main 入口

## 项目结构
dormRepair/
- src/
  - main/
    - java/org/example/dormrepair/
      - mapper/      # 数据访问层（MyBatis Mapper）
      - pojo/        # 实体类（User、RepairTable等）
      - service/     # 业务逻辑层
      - util/        # 工具类
      - Main.java    # 程序入口
      - *Menu.java   # 各角色交互菜单
    - resources/
      - mybatis-config.xml  # MyBatis 核心配置
  - test/              # 单元测试目录
- pom.xml               # Maven 依赖配置
- .gitignore            # Git 忽略文件


## 说说做项目过程
-  这星期刚开始才学到继承，原本决定星期三学完开始做的
-  结果一直刷课刷到星期四才学到MySQL，
-  时间不够了，连忙找ai问问怎么做了
-  星期四晚上先是在datagrip上面建了表
-  然后跟着ai稀里糊涂地就去做了Maven的pom.xml导入了依赖包和搞了MyBatis的配置文件
-  跑去写了pojo,service,mapper,util。分成实体，服务，接口，工具四个类，util就不太懂，直接当固定模板抄过来用了
-  结果星期五晚上发现自己的MySQL是32位版本的，又跑去官方那里下64位，搞了一个多小时
-  后面就一直在赶工，一边写菜单的功能，一边补mapper,service的内容，写方法
-  写到大概12点半才写完菜单，然后继续搞测试，修bug,测到2点多，今天早上又补了一会小功能
-  终于是写完了，燃尽了
-  这周遇到最多问题是接口和数据库没匹配上，每次写mapper，MySQL语句藏在双引号里面，错了也看不出来
-  一开始写完一些基础的东西试运行的时候莫名其妙数据库里面有信息，结果程序出来没有，查了一下才知道MyBatis的下划线和驼峰没开
-  写SELECT写多了，莫名其妙把DELETE写成DELECT
-  感觉查bug也很麻烦，写关于处理进度那一段发现都报问题未知状态，后面才发现把报修单ID存到状态去了
-  总是犯一些小错，错误情况忘记break，switch忘记break
-  到最后我的输入验证也还不是很完善，乱输字符还会报错，同种类型的字符乱输倒是没事，不过时间不够了，下次再改

-  最后提交的时候，开了加速器github不给连，不开又连不上
-  最后跑去问ai,关了SSL再用加速器传上去，再开回来，不知道学长们有没有什么更好的办法？
-  
