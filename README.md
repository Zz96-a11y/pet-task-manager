
# 桌宠任务清单管理项目

## 项目简介
这是一个基于Java Swing实现的的桌面宠物与任务清单管理系统,支持桌宠互动、任务管理、专注计时等功能

## 目录结构说明
- **documents** --  项目开发相关文档（包括项目需求文档、流程图，原型图）
- **projects** --  项目源码

## 环境搭建
**开发工具**
| 工具  | 说明  | 备注  |
|---|---|---|
|  IDEA |  开发IDE | https://gitee.com/link?target=https%3A%2F%2Fwww.jetbrains.com%2Fidea%2Fdownload  |

**开发环境**
| 工具  | 版本  | 备注  |
|---|---|---|
|  Windows | 10  | 操作系统  |
|  JDK | 17  |  https://www.injdk.cn/ |

## 技术栈

- JavaSE
- Swing（GUI）
- JSON（本地数据持久化，手动解析）

## 架构图

```
DeskPet/
+-- com/deskpet/
    +-- Main.java                    # 入口
    +-- model/                       # 数据 + 业务规则
    +-- pojo/
    +-- Task.java            # 任务 数据
    +-- PetState.java        # 宠物 数据
    +-- dao/
    +-- TaskDao.java         # 存文件 / 读文件（pojo <-> JSON）
    +-- service/
    +-- TaskService.java     # 任务：增删改查、校验、计时
    +-- PetService.java      # 宠物：亲密度、升级、冷却
    +-- view/                        # 纯界面，显示 + 收集操作
    +-- PetView.java             # 桌宠透明窗口 + GIF + 右键菜单 + 拖拽
    +-- InteractionView.java     # 互动面板
    +-- TaskView.java            # 任务清单 + 日期导航
    +-- TaskEditor.java          # 任务编辑弹窗（新增/修改共用）
    +-- TimerView.java           # 计时器界面
    +-- GifPlayer.java           # GIF 播放
    +-- controller/                  # 连接 M 和 V
    +-- PetController.java       # 宠物相关
    +-- TaskController.java      # 任务相关
    +-- config/                      # 配置类
```

## 各层说明

### model 层（数据 + 业务规则）

- **pojo**：纯数据类，只存字段和 getter/setter
  - Task.java：任务数据（名称、日期、优先级、预计时长、完成状态、累计专注时长）
  - PetState.java：宠物数据（名字、等级、亲密度、状态、玩耍冷却）
- **dao**：数据访问层，负责文件读写
  - TaskDao.java：任务的 JSON 序列化/反序列化，读/写 data/tasks.json
- **service**：业务逻辑层，核心功能实现
  - TaskService.java：任务增删改查、表单校验、日期筛选、专注计时累加
  - PetService.java：亲密度增减、升级判断、玩耍冷却管理、待机动画

### view 层（纯界面）

- PetView.java：桌宠主窗口（透明、悬浮、可拖拽），右键弹出菜单（互动/任务/设置）
- InteractionView.java：互动面板窗口（亲密度进度条 + 喂食/抚摸/玩耍按钮）
- TaskView.java：任务清单窗口（日期导航 + 任务列表 + 新增按钮）
- TaskEditor.java：任务编辑弹窗（新增和修改共用，表单校验）
- TimerView.java：计时器窗口（大字号时间 + 开始/暂停/结束按钮）
- GifPlayer.java：GIF 动画播放器组件（桌宠待机动画、互动动画）

### controller 层（连接 M 和 V）

- PetController.java：宠物相关的事件监听和逻辑调度，连接 PetService 和 PetView/InteractionView
- TaskController.java：任务相关的事件监听和逻辑调度，连接 TaskService 和 TaskView/TaskEditor/TimerView

### config 层（配置）

- 存放全局配置类，如常量定义、颜色配置、路径配置等

## 操作流程

启动程序 -> PetView 桌宠窗口显示（悬浮桌面）
    | 鼠标右键桌宠
弹出菜单：[互动] [任务] [设置]
    +-- 点击互动 -> 打开 InteractionView 互动面板
    +-- 点击任务 -> 打开 TaskView 任务清单
    +-- 点击设置 -> 打开设置窗口

## 数据持久化

- 任务数据存储在 data/tasks.json，由 TaskDao 负责读写
- 首次运行自动创建文件并初始化示例数据

