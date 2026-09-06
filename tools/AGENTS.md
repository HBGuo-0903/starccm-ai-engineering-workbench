# STAR-CCM+ Development Tools

本目录存放可独立运行的 STAR-CCM+ Java 开发工具和 AI 工具，包括 Skills、MCP 项目及其脚本。参考资料位于同级 `../references`，运行时生成物默认位于用户桌面的 `runtime_yyyyMMdd`，不写入本目录。

## Tool Types

- `skills/`：面向 AI 的工作规范和可复用资源。每个 Skill 必须包含 `SKILL.md`，可选 `agents/openai.yaml`、`scripts/`、`references/` 或 `assets/`。
- `starccm-javadoc-mcp/`：完整的 Node.js MCP 项目，通过本机 `STARCCM_DOC` 或启动参数读取用户合法安装的 STAR-CCM+ Javadoc，提供 `search_api` 和 `get_doc`。

## Skill Responsibilities

- `starccm-script-generator`：读取 `../references/STARCCM_References_Index.csv`，调用官方 Javadoc MCP 验证 API，并生成 STAR-CCM+ Java 脚本。它不刷新参考索引。
- `starccm-knowledge-maintainer`：维护、审计和重组 `../references`；只有它可以调用 `scripts/generate-references-index.ps1` 刷新索引。

## Open-Source Boundary

- 本目录可以公开上传到 GitHub，但不能提交 Siemens 官方 Javadoc、STAR-CCM+ 安装文件、许可证密钥、密码、Token、私有 IP 或本机绝对路径。
- MCP 的依赖目录、缓存和构建输出不属于源代码提交内容；由 `.gitignore` 排除。
- Skill 源文件位于仓库内，若 AI 客户端不自动发现项目内 Skill，应按根目录 `README.md` 的说明安装到对应的用户 Skill 目录。
- 修改工具行为时，必须同步检查 Skill 的路径解析、MCP 的启动参数和跨机器使用方式。
