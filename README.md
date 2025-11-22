# AE2 Storage DB

AE2 Storage DB 是一个 Minecraft Forge 模组，为 Applied Energistics 2 (AE2) 添加了基于数据库的存储系统。该模组扩展了 AE2 的存储功能，提供了一种新的存储方式。

## 功能特性

- **数据库存储单元**: 提供物品和流体数据库存储单元，支持高效的存储管理。
- **存储驱动器**: 基于数据库的存储驱动器，用于容纳和组织存储单元。
- **存储元件**: 数据库存储元件，用于构建自定义存储解决方案。
- **兼容性**: 完全兼容 AE2 的网络系统和存储机制。

## 系统要求

- Minecraft 1.18.2
- Forge 40.2.17 或更高版本
- Applied Energistics 2 (AE2) 11.6.1-beta 至 12.0.0 版本

## 安装

1. 确保您已安装 Minecraft Forge 40.2.17 或兼容版本。
2. 下载并安装 Applied Energistics 2 (AE2) 模组。
3. 将 AE2 Storage DB 的 JAR 文件放入您的 `mods` 文件夹中。
4. 启动 Minecraft 并享受新的存储功能！

## 构建

如果您想从源代码构建此模组：

1. 克隆此仓库：
   ```
   git clone https://github.com/your-repo/ae2-storage-db-forge.git
   ```

2. 进入项目目录：
   ```
   cd ae2-storage-db-forge-main
   ```

3. 使用 Gradle 构建：
   ```
   ./gradlew build
   ```

4. 构建后的 JAR 文件位于 `build/libs/` 目录中。

## 使用指南

### 存储单元
- **物品数据库存储单元**: 用于存储物品，支持 AE2 网络中的物品传输。
- **流体数据库存储单元**: 用于存储流体，支持 AE2 网络中的流体传输。

### 存储驱动器
- 将存储单元放入基于数据库的存储驱动器中。
- 驱动器会自动管理存储单元的容量和内容。

### 存储元件
- 使用数据库存储元件来增强您的存储系统。
- 可以与 AE2 的其他元件结合使用。

## 开发

此模组使用 Java 编写，基于 Minecraft Forge 开发框架。

### 项目结构
- `src/main/java/`: Java 源代码
- `src/main/resources/`: 资源文件（纹理、模型、语言文件等）

### 贡献
欢迎提交 Issue 和 Pull Request！请确保您的代码符合项目的编码标准。

## 许可证

此项目采用 GPL-3.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 致谢

- 感谢 Applied Energistics 2 团队提供优秀的模组框架。
- 感谢 Minecraft Forge 社区的支持。

## 支持

如果您遇到问题或有建议，请在 GitHub 上提交 Issue。