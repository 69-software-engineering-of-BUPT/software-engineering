咕咕嘎嘎
---------------------------------------------------------------------------------------
输入
http://localhost:8080/jsp/motest_login.jsp
理论上所有不报错的的页面在未登录下都会跳转这个
注册和登录页面仅用于测试，用的HttpSession，之后登录页面换
目前只有发布，查看job，下架，删除，查看申请,修改状态，提出反馈
（我印象里通知和过滤没做）
jobId用于识别工作，一般jobs的json文件名JOB_jobId
理论上正常流程是有的
不是所有按钮都实现了所以有的按钮可能按了没反应正常（比如过滤）
界面长啥样我没管，之后再说，现在先有着就行


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