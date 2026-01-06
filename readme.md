# WiamSubtitleMaker - 智能字幕生成器 / WiamSubtitleMaker - Intelligent Subtitle Generator

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21+-007396?logo=java&logoColor=white)](https://openjfx.io/)
[![Gradle](https://img.shields.io/badge/Gradle-8.0+-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

**一款基于 AssemblyAI API 的智能字幕生成工具，支持语音识别、字幕编辑** / **An intelligent subtitle generation tool based on AssemblyAI API, supporting speech recognition and subtitle editing**

[English Version / 英文版](./readme_en.md)

![主界面演示](ui.png)

## 📋 项目简介

WiamSubtitleMaker 是一款功能强大的智能字幕制作工具，利用 AssemblyAI 的先进语音识别技术，为视频内容自动生成高质量字幕。该工具提供直观的图形用户界面，支持字幕的精细化编辑和调整，帮助用户快速完成字幕制作工作。

## ✨ 主要功能

- **语音识别**: 集成 AssemblyAI API，支持高质量语音转文字
- **图形界面**: 基于 JavaFX 的直观用户界面，操作简单便捷
- **字幕编辑**: 支持句子合并、分割等精细化编辑功能
- **多格式支持**: 支持多种音视频格式输入，输出标准 ASS 字幕格式
- **实时预览**: 提供转录片段和置信度信息，便于质量控制
- **灵活配置**: 支持 API 密钥管理和配置保存

## 🚀 快速开始

### 系统要求

- Java 21 或更高版本（需包含 JavaFX）
- 有效的 AssemblyAI API 密钥
- 稳定的网络连接

### 安装步骤

1. 克隆或下载项目代码
2. 确保已安装 Java 21 或更高版本
3. 使用 Gradle 构建项目：
   ```bash
   ./gradlew build
   ```
4. 运行应用程序：
   ```bash
   ./gradlew run
   ```
5. 或者打包为可执行JAR文件：
   ```bash
   ./gradlew shadowJar
   ```
   然后运行生成的JAR文件
6. 当然下载 release 然后点两下也可以运行

### 配置 API 密钥

1. 访问 [AssemblyAI](https://www.assemblyai.com/) 注册账户并获取 API 密钥
2. 在应用界面中输入 API 密钥并保存
3. 密钥将自动保存到 [config.json](./config.json) 文件中

## 📖 使用指南

### 1. 文件上传与转换

1. 点击"选择文件"按钮，选择需要生成字幕的音视频文件
2. 点击"上传文件"将文件上传至 AssemblyAI 服务器
3. 点击"转换"按钮开始语音识别过程

### 2. 结果查询与编辑

1. 点击"查询转换"获取转换任务列表
2. 选择转换任务后点击"获取结果"获取句子列表
3. 在句子列表中可以查看详细的转录片段信息

### 3. 字幕编辑功能

- **合并句子**: 选择句子后点击"合并句子"按钮，将当前句子与下一句合并
- **分割句子**: 选择句子中的特定转录片段，点击"分割句子"按该点分割句子
- **查看关联**: 句子列表和转录片段表格支持联动选择

### 4. 导出字幕

1. 编辑完成后点击"保存为字幕事件"按钮
2. 选择保存位置和文件名
3. 系统将生成 Aegisub 的字幕事件
4. 复制到 Aegisub 中使用

## 📁 项目结构

```
WiamSubtitleMaker/
├── src/main/kotlin/                    # 源代码目录
│   ├── AssemblyAIService.kt            # AssemblyAI API 服务封装
│   ├── Config.kt                       # 配置管理
│   ├── Main.kt                         # 应用入口
│   ├── Player.kt                       # 播放器组件
│   ├── Sentence.kt                     # 句子数据结构
│   ├── SubtitleEvent.kt                # 字幕事件数据结构
│   ├── TranscriptSegment.kt            # 转录片段数据结构
│   ├── WTime.kt                        # 时间管理
│   └── user_interaction/               # 用户界面交互组件
│       ├── DoForMessage.kt             # 消息处理
│       ├── ListViewGenerator.kt        # 列表视图生成器
│       ├── Message.kt                  # 消息类型定义
│       ├── ResultedButton.kt           # 响应式按钮
│       ├── TableViewGenerator.kt       # 表格视图生成器
│       ├── UIComponentAction.kt        # UI组件操作
│       ├── UIComponentData.kt          # UI组件数据
│       └── UIComponentDisplay.kt       # UI组件显示
├── src/test/kotlin/
│   └── Test.kt                         # 测试文件
├── build.gradle                        # Gradle 构建脚本
├── config.json                         # API 密钥配置文件
├── gradlew                             # Gradle 包装器脚本
├── gradlew.bat                         # Gradle 包装器批处理脚本
├── settings.gradle                     # Gradle 设置
├── input/                              # 输入文件目录
├── output/                             # 输出文件目录
├── readme.md                           # 项目说明文档(中文)
├── readme_en.md                        # 项目说明文档(英文)
└── ui.png                              # 界面截图
```

## ⚙️ 构建配置

本项目使用 Gradle 进行构建管理，主要依赖包括：

- Kotlin 2.0+
- JavaFX 21+
- Ktor HTTP 客户端
- kotlinx.serialization
- AssemblyAI API 客户端

## 🔧 高级功能

### 从文件加载结果
- 支持从 JSON 文件加载已保存的句子列表
- 点击"从文件获取结果"按钮选择 JSON 格式的句子数据文件

### 置信度检查
- 转录片段表格显示每个片段的置信度
- 有助于评估转录质量

### 字幕格式导出
- 支持将生成的字幕导出为 Aegisub 兼容格式
- 生成标准的字幕事件，便于后续编辑和使用

## 🔒 安全说明

- API 密钥仅在本地存储，请妥善保管 [config.json](./config.json) 文件
- 上传的文件将传输至 AssemblyAI 服务器进行处理

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 📄 许可证

本项目遵循 MIT 许可证 - 详见 [LICENSE](./LICENSE) 文件

## 🙏 鸣谢

- [AssemblyAI](https://www.assemblyai.com/) - 提供高质量的语音识别 API
- [Kotlin](https://kotlinlang.org/) - 现代编程语言
- [JavaFX](https://openjfx.io/) - 丰富的客户端应用程序平台
- [Ktor](https://ktor.io/) - 用于连接 AssemblyAI API 的 HTTP 客户端

## 作者声明

- 本项目使用 [AssemblyAI](https://www.assemblyai.com/) API 进行语音识别
- 协助创建此模组的AI编码助手: [Lingma](https://lingma.aliyun.com/lingma)

---

*伟大的全自动字幕生成器，旨在打破邪恶的美利坚资本主义的语言壁垒和技术垄断*

*我这么有贡献，国家应该给我特批一个超级共和国勋章外加北京三套房*