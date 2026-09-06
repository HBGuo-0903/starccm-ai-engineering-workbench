# STAR-CCM+ Java Development Repository

本仓库同时包含 STAR-CCM+ Java 参考资料、可复用 Skill 和官方 Javadoc 查询 MCP。它面向公开 GitHub 分发；不得把本机绝对路径、许可证、密码、Token、私有 IP 或未授权的 Siemens 文档内容提交到仓库。

## Repository Map

- `references`：按功能组织的基础语法、STAR-CCM+ 内部宏、外部运行项目和共享库。
- `references/STARCCM_References_Index.csv`：活动参考资料的导航索引，`RelativePath` 使用 `references` 目录相对路径。
- `tools/skills/starccm-script-generator`：使用本地参考资料和官方 Javadoc 生成 STAR-CCM+ 脚本的 Skill。
- `tools/skills/starccm-knowledge-maintainer`：审计和维护参考资料的 Skill；只有该 Skill 负责刷新索引。
- `tools/starccm-javadoc-mcp`：从本机 STAR-CCM+ 安装目录读取 Javadoc 并提供 `search_api`、`get_doc` 的 MCP 项目。

## Usage Boundary

先阅读 `references/AGENTS.md` 和 `references/STARCCM_References_Index.csv`，再根据任务选择需要深入阅读的范围。生成脚本的默认产物写入用户桌面的 `runtime_yyyyMMdd` 目录，不写入仓库。

MCP 只读取用户本机已有的 STAR-CCM+ Javadoc；本仓库不分发 Siemens 官方 Javadoc。Skill、MCP、参考资料和运行时产物必须保持职责分离。
