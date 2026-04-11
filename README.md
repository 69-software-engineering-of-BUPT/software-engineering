# software-engineering

the best programmers

| Github Username | QMID      |
| :-------------- | :-------- |
| gdt1205         | 231225959 |
| Gaojie13        | 231225926 |
| Ddark-123       | 231225753 |
| MasterYibo      | 231225948 |
| Kerui-aua       | 231225845 |
| wangyanzhou     | 231225937 |

***

## 🚀 项目启动说明

本项目基于 Java Servlet/JSP 构建，不依赖外部数据库，所有数据均存储在本地 JSON/CSV 文件中。

### 运行环境要求

- **Java**: JDK 11 或以上
- **构建工具**: 已经内置了 Maven Wrapper (`mvnw`)，不需要额外安装 Maven

### 怎么把项目跑起来？

我们配置了嵌入式的 Tomcat7 插件，所以启动非常简单：

**方法 1：在 VS Code 中一键启动（推荐）**

1. 按 `Ctrl + Shift + P`（或 `F1`）打开命令面板
2. 输入并选择 `Run Task`（运行任务）
3. 在弹出的列表中选择 **`Run Tomcat`**
4. 等待终端输出 `Starting ProtocolHandler ["http-bio-8080"]`，说明服务器已经启动成功了
5. 打开浏览器访问：<http://localhost:8080/>

**方法 2：通过 IDEA 启动**
如果你习惯用 IntelliJ IDEA，可以直接在右侧边栏找到 **Maven** 面板，依次展开 `ta-recruitment-system` -> `Plugins` -> `tomcat7`，然后双击 `tomcat7:run` 即可。

**方法 3：在普通命令行启动**
在项目根目录（`pom.xml` 所在的目录）下，直接运行：

- Windows: `.\mvnw.cmd tomcat7:run`
- Mac/Linux: `./mvnw tomcat7:run`

***

## 📂 项目代码结构

为了方便大家分工合作，代码和数据都做了明确的分层。大家写代码的时候直接去对应的包里写就行。

```text
software-engineering/
├── src/main/java/com/bupt/tarecruit/
│   ├── controller/   # 🌟 所有的 Servlet 都写在这里，负责接收请求、跳转页面
│   ├── service/      # 🌟 核心业务逻辑（比如“处理申请”、“校验3门课上限”）写在这里
│   ├── repository/   # 📦 数据访问层，负责把 Model 对象存进 JSON，或者从 JSON 读出来
│   ├── model/        # 📦 实体类（User, Job, Application, Notification）
│   └── util/         # 🛠️ 工具类，比如读写 JSON 的 JsonUtil 就在这里
│
├── src/main/webapp/
│   ├── WEB-INF/      # web.xml 路由配置
│   ├── jsp/          # 🎨 所有的网页视图（JSP）写在这里
│   ├── css/          # 静态样式
│   └── js/           # 静态脚本
│
├── data/             # 🗄️ 系统的“伪数据库”，所有数据按类别存成 .json 文件
│   ├── users/        # 存 TA、MO、Admin 的账号信息
│   ├── jobs/         # 存 MO 发布的岗位
│   ├── applications/ # 存 TA 提交的申请记录
│   └── notifications/# 存系统发出的通知
│
├── exports/          # 管理员导出的 CSV 文件默认存这里
├── logs/             # 系统操作日志
└── uploads/          # TA 上传的 CV 简历文件存这里
```

### 💡 开发约定

1. **不要上数据库**：项目严格按照 Handout 要求，所有数据落地必须走 `repository` 存成 JSON。
2. **前后端分层开发**：`controller` 里只做参数获取和页面转发（`request.getRequestDispatcher`），复杂的逻辑判断丢给 `service` 去做。
3. **测试数据**：目前 `data/` 目录下已经准备了几个测试用的账号和数据，大家写页面的时候可以直接读取这些数据来测试渲染效果。

