# TMA · 电话营销助理

> **⚠️ 本项目已停止更新，归档保留。**
>
> TMA 是早期的纯单机版电销助手，数据全部存在手机本地。后续已由 **[知行同步助手](https://github.com/x2it/zhixing-circle)** 接替——在保留拨号跟进核心体验的基础上，增加了云端同步、通讯录/短信/通话双向备份、标签分层体系和 Web 管理端。
>
> - 📱 Android 客户端：[x2it/zhixing-sync-assistant](https://github.com/x2it/zhixing-sync-assistant)
> - 🌐 服务端 + Web 端：[x2it/zhixing-circle](https://github.com/x2it/zhixing-circle)
>
> 新项目仍在活跃维护，建议直接使用知行同步助手。以下文档仅供历史参考。

---

> **Only the next call.** — 专注下一通电话，其余交给系统。
>
> Android 原生电销助手，半自动拨号 + 通话后跟进登记 + 客户档案管理。

---

## 概述

TMA 是一款面向房产经纪人/电话销售人员的 Android 应用。核心理念：**用最少的操作完成"拨号 → 登记 → 下一位"循环**。

- 数据全部存储在本地（Room/SQLite），不上传任何服务器
- 不使用无障碍服务，不自动拨/挂机，不提供机器人外呼
- 遵循奥卡姆剃刀原则：界面无冗余元素，功能必须真实可用

## 功能

### 工作台
客户总数、今日已拨打/未拨打、过期跟进、今日跟进统计、意向分布图。

### 客户名单
搜索（姓名/电话/楼盘/区域）、筛选（意向等级/待跟进/队列/今日已拨打/未拨打）、新建/编辑/删除、一键批量入队。

### 拨号队列
按顺序拨打，支持单条入队/出队、批量入队、清空。队列编号自动重排，永不跳号。

### 半自动拨号 + 跟进登记
- ACTION_DIAL 跳系统拨号盘（默认），可选 ACTION_CALL 直接拨打
- PhoneStateListener 监听通话状态，挂断后自动弹出跟进登记
- 登记结果（已接通/未接通/已约看/已拒绝/空错号/关停机/待跟进）
- 自动更新意向等级和下次跟进时间

### 数据导入导出
- CSV / XLSX 导入（手机号自动去重，无效号码报告）
- CSV / XLSX 导出（全部或筛选结果）
- 通讯录导入/导出
- 导入模板下载（含中文字段表头 + 示例数据）

### 合规
首次启动需确认合规声明。明确禁止违规/骚扰使用。

## 技术栈

| 模块 | 技术 |
|---|---|
| UI | Jetpack Compose + Material 3（扁平暗色主题） |
| 架构 | MVVM + Hilt + Coroutines + Flow |
| 存储 | Room (SQLite)，TypeConverters |
| 导航 | Navigation Compose 2.8.1 |
| 电话 | ACTION_DIAL / PhoneStateListener |
| 数据格式 | OpenCSV + Apache POI（XLSX 导入）/ 纯 Kotlin XlsxWriter（XLSX 导出/模板） |
| 最低系统 | Android 12 (API 31) |
| 目标系统 | Android 15 (API 35) |

## 快速开始

### 环境要求
- Android Studio Hedgehog 或更新
- JDK 17
- Android SDK Platform 35

### 构建步骤

```bash
# 1. 克隆仓库
git clone https://github.com/x2it/TMA.git
cd TMA

# 2. 配置 SDK 路径
cp local.properties.example local.properties
# 编辑 local.properties，填入你的 SDK 路径：
# sdk.dir=C\:\\Users\\<YOU>\\AppData\\Local\\Android\\Sdk

# 3. 构建 Debug APK
.\gradlew.bat assembleDebug    # Windows
./gradlew assembleDebug        # macOS/Linux

# 4. 构建 Release APK（默认使用 debug 签名，仅用于测试）
.\gradlew.bat assembleRelease

# 产物路径
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release.apk
```

> **签名说明**：当前 release 构建默认使用 debug 签名。如需正式签名分发，请在 `app/build.gradle.kts` 中配置自己的 keystore。

### 首次使用流程
1. 安装并打开 App → 确认合规声明
2. 进入「数据」页 → 下载导入模板
3. 用 Excel 填写客户数据 → 导入 XLSX
4. 进入「名单」→ 点击「加入队列」
5. 进入「队列」→ 点击「拨号」→ 系统拨号盘拨打
6. 通话结束 → 自动弹出跟进登记 → 选结果 → 保存
7. 回到队列继续下一通

## 数据字段

CSV/XLSX 共用中文表头（严格按此顺序）：

```
姓名、手机号、备用电话、性别、年龄、微信、来源、意向区域、
预算(万)下限、预算(万)上限、房型、意向楼盘、
意向等级(A/B/C/D/U)、备注、下次跟进(YYYY-MM-DD)
```

**意向等级**：
- **A** 强烈意向（绿） / **B** 一般（蓝） / **C** 弱（黄） / **D** 无效（红） / **U** 未评级（灰）
- 不填默认 U，大小写均可

**手机号**：自动去除 `-`、空格、`+86` 前缀后去重。11 位大陆号为首选格式。

## 项目结构

```
app/src/main/java/com/realtor/geeksales/
├── GeekSalesApp.kt              # Application 入口
├── MainActivity.kt               # 单 Activity + NavHost + BottomBar
├── navigation/Routes.kt          # 路由定义 + safeNavigate
├── ui/
│   ├── theme/                    # 颜色、字体、主题
│   ├── components/               # 通用组件（按钮、卡片、徽标、Toast）
│   └── screen/                   # 各页面
│       ├── DashboardScreen       # 工作台
│       ├── CustomerListScreen     # 客户名单
│       ├── CustomerDetailScreen   # 客户详情
│       ├── CustomerEditScreen     # 新建/编辑
│       ├── DialQueueScreen        # 拨号队列
│       ├── ImportExportScreen     # 导入导出
│       ├── FollowUpDialog         # 跟进登记
│       ├── SettingsScreen         # 设置
│       └── ComplianceScreen       # 合规声明
├── viewmodel/                    # MVVM ViewModel
├── data/
│   ├── db/                       # Room 实体、DAO、数据库
│   ├── repo/                     # Repository
│   └── importexport/             # CSV/XLSX 导入导出
├── telephony/                    # 拨号、通话监听
├── util/                         # 工具类
└── di/                           # Hilt 依赖注入
```

## 合规与隐私

- 不使用无障碍服务，不自动拨/挂机
- 数据全部本地存储，卸载即删除
- 使用请遵守《个人信息保护法》及运营商反骚扰要求
- 仅致电本人授权或合法来源的客户

## 后续项目

TMA 已归档，其功能由 **知行同步助手** 系列接替：

| 仓库 | 说明 |
|---|---|
| [x2it/zhixing-sync-assistant](https://github.com/x2it/zhixing-sync-assistant) | Android 客户端（Kotlin + Compose），在 TMA 拨号跟进基础上增加云端同步 |
| [x2it/zhixing-circle](https://github.com/x2it/zhixing-circle) | 服务端 + Web 管理端（NestJS + React + PostgreSQL） |

## License

MIT License - 见 [LICENSE](LICENSE)

## 致谢

© 2026 知行工作室