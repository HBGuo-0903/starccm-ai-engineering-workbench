# External STAR-CCM+ Runners

## Scope

本目录保存由 STAR-CCM+ 外部环境启动、控制或协同运行的完整项目。外部入口包括 Python、PowerShell、Shell、Batch、Slurm、其他调度器、Java 外部程序、远程 `ServerConnection` 和外部求解器。

仅在 STAR-CCM+ 内部加载并执行的 Java 宏，放在 `Standalone_STARCCM_Functions`。单独使用 STAR-CCM+ 的 solver、physics 或 coupling API，并不自动属于本目录；只有存在外部进程、远程连接、集群提交、外部求解器或外部启动链时，才归入本目录。

## Organization Rules

- 根目录下每个子目录必须对应一个完整的外部项目或边界清晰的外部运行能力。
- 按功能和运行边界命名，不按 GitHub 仓库名、下载顺序、仓库序号或 `sources 1`、`sources 2` 命名。
- 同一外部项目的 Java、Python、Shell、Batch、Slurm、配置、输入、模板、构建文件、日志和后处理文件必须整体保存。
- 同一项目的不同运行阶段不能拆成不同目录；彼此无关的项目才应拆分。
- 如果项目中的 Java 宏依赖外部启动器、配置或输入文件，Java 宏必须随项目保留，不能移入 `Standalone_STARCCM_Functions`。
- 当前可用目录只保留最新版本；测试、临时尝试、旧版本和重复副本移入 `98_Previous_Organization`。

## Project Boundary

一个外部项目的最小内容是完成原始运行链所需的全部文件，通常包括：

- 外部启动器和参数解析；
- STAR-CCM+ Java 宏或宏库；
- 外部求解器、协同仿真端、通信和映射代码；
- 集群、队列、资源和环境配置；
- `.sim`、几何、表格、模板和参数文件；
- 结果提取、后处理、日志收集和验证脚本；
- 构建文件、依赖文件和运行文档。

如果删除某类文件后项目无法按原流程运行，该类文件就必须留在同一个项目目录中。不能只保留 Java 文件而丢失启动器、构建文件、输入模板或外部配置。

## Directory Layout

外部资料可以采用以下两种合法结构。

### Standardized reference layout

适用于已经按功能整理的资料：

```text
<External_Function>/
├─ Code/              # Java 或外部程序代码
├─ Input_Files/       # 有真实输入文件时创建
├─ Documentation/     # 有独立技术资料时创建
├─ README.md
└─ AGENTS.md
```

### Complete project layout

适用于必须保持原始构建和运行关系的项目。项目可以保留 `src`、`nbproject`、`Launcher`、构建文件和多个语言目录，不得为了套用标准结构而拆分、删除或重命名这些内容。

`Code`、`Input_Files` 和 `Documentation` 不是强制目录；只有实际需要时才创建。每个项目根目录必须有 `README.md` 和 `AGENTS.md`。

## Documentation Rules

### README.md

面向普通用户，至少说明：

- 通过什么外部入口启动 STAR-CCM+；
- 适用场景，如批处理、多案例、参数扫描、HPC、协同仿真或结果提取；
- 所需软件版本、平台、环境变量、队列和权限；
- simulation、参数、路径、端口和输入文件；
- 实际运行或提交命令；
- Java 宏、外部脚本和后处理之间的执行顺序；
- 日志、作业状态、结果文件和成功判据；
- 参考的开源项目或仓库名称。

### AGENTS.md

面向维护者，必须包含以下章节：

- `Summary`：通过什么外部方法实现什么功能，解决什么问题；
- `Input`：命令参数、环境、simulation、输入文件、端口、资源和路径；
- `Output`：作业、日志、仿真结果、导出文件和后处理结果；
- `Workflow`：只包含 Mermaid 代码块，描述真实外部启动链和内部执行链；
- `File Roles`：Java、脚本、配置、输入、构建和后处理文件的职责；
- `Constraints`：版本、平台、集群、路径、依赖和已知限制。

## Summary Rules

外部项目的 Summary 必须同时说明：

1. 通过什么外部入口、调度或通信方法运行；
2. 实现什么具体功能；
3. 解决什么工程问题或使用场景。

推荐句式：

`本项目通过 <外部入口/调度/通信方法> 启动并控制 <STAR-CCM+ 或协同组件>，实现 <具体功能>，解决 <明确工程问题或使用场景>。`

仅写“从外部调用 STAR-CCM+”“提交一个作业”“运行 Java 宏”，或只列出 `sbatch`、`ServerConnection`、`Simulation` 和脚本名，均不合格。

## Workflow Rules

`Workflow` 只能包含 Mermaid 代码，不得在代码块之外附加普通段落、编号步骤、项目符号或“输入 → 启动 → 输出”的抽象流程。

Mermaid 必须根据实际代码、配置和命令编写，并表现：

- 外部入口文件、命令、参数和工作目录；
- 环境变量、STAR-CCM+ 可执行文件、Java 类路径和依赖；
- `.sim`、几何、参数、输入表格、宏和配置文件关系；
- 作业提交、进程启动、并行参数、队列资源和远程连接；
- Java 宏真实入口、执行顺序和关键 STAR-CCM+ 对象操作；
- 协同仿真中的连接、数据交换、同步、映射和失败分支；
- 多案例、参数扫描、循环、重试、并行/串行阶段和异常处理；
- 日志、退出码、作业状态、结果文件和后处理验证；
- 可选步骤与必需步骤之间的条件关系。

节点必须尽可能使用真实文件名、命令、参数、类名、方法名、路径变量、作业工具和输出文件名。`启动脚本`、`运行宏`、`交换数据`、`检查结果` 等抽象节点不能代替真实执行链。

包含括号、冒号、斜线、反斜线、逗号或中文长文本的节点，必须使用带双引号的标签，例如 `A["Launcher/tirion.py --simulation case.sim"]`。不要使用 `execute\(\)` 代替 `execute()`，也不要在未加引号的节点标签中直接放置特殊字符。

## Environment and Safety

- 绝对路径、队列名、节点名、端口、安装路径和外部求解器路径必须标明为需要按环境修改的配置。
- 不得保存密码、令牌、私钥或真实凭据；使用环境变量名、占位符和脱敏示例。
- 必须说明 Windows、Linux、集群节点和远程主机上的执行位置，不能把不同平台的命令混写成默认命令。
- 脚本退出码成功不代表仿真成功；必须同时检查作业状态、STAR-CCM+ 日志、通信状态和结果文件。
- 保留原始构建结构和依赖关系，不得为了目录整齐删除 `src`、`nbproject`、`Launcher`、构建文件或配置文件。

## Validation

整理或修改后检查：

- 每个根目录子目录都是完整项目或边界清晰的外部运行能力；
- 同一项目的 Java、外部脚本、配置、输入和后处理文件没有被拆开；
- 原始入口、构建结构和依赖关系仍然存在；
- 每个项目有 `README.md` 和 `AGENTS.md`；
- `Summary`、`Input`、`Output`、`File Roles` 和 `Constraints` 与实际文件一致；
- `Workflow` 是 Mermaid-only，并含真实命令、参数、类、文件、顺序、分支和验证步骤；
- Mermaid 特殊字符已正确处理；
- 只在 STAR-CCM+ 内运行的宏没有误放在本目录；
- 没有空的 `Input_Files`、`Documentation`、测试、旧版本、重复内容、凭据或临时文件。
