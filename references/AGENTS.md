# STAR-CCM+ Java Development References

本目录是 STAR-CCM+ Java 二次开发参考资料库。资料按具体功能和运行方式组织，不按 GitHub 仓库组织；功能目录和子目录不使用序号。

## Directory Categories

- `Basic_Syntax`：STAR-CCM+ 宏基础、Java API 基础用法和小型语法示例。
- `Standalone_STARCCM_Functions`：在 STAR-CCM+ 内部加载并运行、用于完成具体功能的宏。
- `External_STARCCM_Runners`：从外部调用 STAR-CCM+ 的脚本、程序、调度器和耦合项目。
- `Shared_STARCCM_Libraries`：被多个案例复用的 STAR-CCM+ Java 公共库。

## Mandatory Index-First Workflow

每次处理本目录中的资料时，必须先读取：

`references/STARCCM_References_Index.csv`

不得一开始就递归扫描整个 `references` 目录，也不得只根据文件夹名称猜测内容。必须先从 CSV 中确认当前活动资料的分类、项目或文件名称、相对路径、说明、文件类型和文件数量，再根据用户目标确定深入阅读范围。

索引由以下脚本生成：

`tools/skills/starccm-knowledge-maintainer/scripts/generate-references-index.ps1`

只有 `starccm-knowledge-maintainer` Skill 负责调用该脚本刷新索引。其他 Skill 和普通任务只读取现有 CSV；如果发现索引缺失、无法解析、路径失效或明显过期，应报告需要先运行知识库维护 Skill，不自行调用刷新脚本。索引 CSV 本身不作为参考代码或文档内容读取，也不计入项目文件统计。

## Scope Selection Before Deep Reading

读取索引后，必须将用户目标映射到一个或多个具体范围：

1. 先选择相关的 `Category`。
2. 再根据 `ItemName`、`RelativePath` 和 `Description` 选择候选项目或文件。
3. 优先读取候选项目的 `AGENTS.md` 和 `README.md`，确认该目录的边界、输入、输出和工作流。
4. 对代码任务，再读取入口 Java 文件及其直接依赖；对输入或文档任务，只读取与目标相关的输入文件和文档。
5. 只有在引用关系、版本问题或分类判断无法确定时，才扩大到同一类别中的其他项目。

“深入阅读”必须以实际任务为依据。例如：

- 查询 MacroUtils 用途：重点读取 `Shared_STARCCM_Libraries/MacroUtils` 的项目说明、构建文件和相关源码，不需要读取所有案例。
- 分析某个内部宏：重点读取 `Standalone_STARCCM_Functions` 中对应的功能目录、入口宏、直接依赖和该目录的说明文件。
- 分析外部运行流程：重点读取 `External_STARCCM_Runners` 中对应完整项目的入口脚本、Java 代码、输入文件和项目说明；不得把外部项目拆开后跨目录拼接成一个新项目。
- 查找基础 API 示例：优先读取 `Basic_Syntax` 中索引标记的相关 Java 文件或 Markdown 文档。

## Reading and Classification Rules

- CSV 是范围选择和导航入口，不替代对实际文件内容的阅读；做出功能、依赖、重复或可运行性结论前，必须读取对应实际文件。
- 需要判断一个目录是否为单一项目时，必须同时检查其入口文件、依赖文件、构建配置和输入文件，而不能只看 Java 文件数量。
- 同一个项目的多个 Java 文件应作为一个整体分析；多个 Java 文件可能是入口、步骤、辅助类、数据结构或版本兼容代码，不应仅因文件数量多就拆分。
- `MacroUtils` 必须保持原始项目结构；其中的 `SimpleHexaMesher` 属于该项目的子模块。
- `StarClasses_Helper_Library` 是案例级辅助类集合，不是 Siemens 官方帮助文档或通用标准库。
- FSI、Abaqus、集群、代理服务器和协同仿真项目的完整内容属于 `External_STARCCM_Runners`，不能把同一项目的一部分移入其他类别。
- 简单到只展示一两个原生 API 调用的代码，应按 `Basic_Syntax` 处理，不应包装成完整案例。

## Change and Validation Rules

- 任何重组、移动、删除或改名之前，先通过索引确认目标路径和项目边界，并检查目标目录的 `AGENTS.md`。
- 修改代码或文档后，应重新运行索引生成脚本，确认 CSV 中的路径、文件数量和分类已反映最新状态。
- 需要报告完成时，至少说明：实际读取的范围、修改的文件、索引生成结果，以及尚未验证的 STAR-CCM+ 运行条件。
- 不把 `backup` 中的历史副本当作当前活动项目；只有在追溯来源、比较历史版本或恢复文件时才读取它。
- 不删除现有用户文件，除非用户明确要求；清理历史版本时优先移入已有归档目录并记录原因。
