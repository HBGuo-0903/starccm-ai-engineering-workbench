# STAR-CCM+ Standalone Functions

本目录保存**可以直接在 STAR-CCM+ 内部运行的 Java 宏和完整宏案例**，用于学习、验证 API、自动化建模和后处理。这里的“Standalone”表示宏的主要运行入口是 STAR-CCM+，而不是表示它只能有一个 Java 文件。

## 放置原则

凡是满足下面条件的内容，都放在本目录：

- 通过 STAR-CCM+ 的宏机制直接加载或运行；
- 主要使用 STAR-CCM+ Java API 操作当前 simulation；
- 不依赖外部启动器、集群提交、远程服务器连接或其他外部求解器；
- 可以作为一个独立功能、独立案例或 STAR-CCM+ 内部宏工作流来理解。

不满足这些条件的内容，不放在本目录：

- 从 PowerShell、Batch、Python、Shell、Slurm 或其他程序启动 STAR-CCM+ 的项目；
- 集群、远程 ServerConnection、外部 API、外部求解器和协同仿真项目；
- 需要与外部程序共同运行的完整工作流；
- 被多个案例共同引用的公共 Java 库。

它们分别放在同级目录：

- `External_STARCCM_Runners`：外部启动、集群、远程连接、协同仿真和完整外部工作流；
- `Shared_STARCCM_Libraries`：跨案例复用的公共 Java 库；
- `00_Official_Documentation_21.02.007-R8`：官方文档副本。

## 当前目录结构

当前采用扁平结构：`Standalone_STARCCM_Functions` 根目录下直接放置每一个独立功能或完整案例，不再使用功能分类中间层。目录名按实际功能或案例命名，不使用仓库名、来源名或 Demo 序号作为主要名称。

```text
Standalone_STARCCM_Functions
├─ Couette_Cell                          # 一个独立功能或案例
│  ├─ Code
│  ├─ README.md
│  └─ AGENTS.md
├─ Fluid_Flow_Workflow                   # 一个完整的多步骤案例
│  ├─ Code
│  ├─ Input_Files                         # 仅在确有输入文件时存在
│  ├─ README.md
│  └─ AGENTS.md
└─ ...
```

根目录下的每一个子目录都是实际整理和使用的最小功能单元。如果一个功能目录中的 Java 文件无法证明属于同一个案例或工作流，就必须继续拆分；不能因为它们都属于“可视化”或“网格”而混放在同一个 `Code` 中。

同样，不能因为一个 Java 文件能够在 STAR-CCM+ 中运行，就自动把它保留为独立案例。只有一个或几个基础 API 调用、没有独立输入输出和工作流价值的宏，应直接放到同级 `Basic_Syntax` 根目录，并依靠 Java 文件内的注释说明；不为它创建 `README.md`、`AGENTS.md` 或案例子目录。原始目录保留在 `98_Previous_Organization\Simplified_Basic_Functions`，不进入活动案例集合。

同样，不能因为一个 Java 文件能够在 STAR-CCM+ 中运行，就自动把它保留为独立案例。只有一个或几个基础 API 调用、没有独立输入输出和工作流价值的宏，应提炼到同级 `Basic_Syntax` 的主题文档中；原始目录保留在 `98_Previous_Organization\Simplified_Basic_Functions`，不进入活动案例集合。

## 功能方向

根目录下不再设置固定的功能分类目录。功能类别通过目录名称和各目录的 `README.md` 体现；这样可以直接进入目标功能，不需要先穿过一个与案例无关的分类层。

主要功能方向包括流动、网格与表面处理、物理模型与边界、报告与导出、场景与可视化、求解控制、属性后处理、教学演示和 CAD 工作流。具体功能通过根目录下的案例名称直接识别。

## 命名规则

- 删除 `Macro_`、`Demo_Demo1_`、`Demo_Demo10_` 等没有功能信息的前缀；
- 使用 `Create_Line_Probe`、`Export_Monitors`、`Set_Time_Step` 这类“动作_对象”或“动作_对象_条件”的名称；
- 完整案例使用 `Fluid_Flow_Workflow`、`Porous_Media_Flow_Workflow` 这类能够表达案例范围的名称；
- 保留必要的工程名称、模型名称和技术缩写，例如 `NACA_2412`、`DFBI_Boats`、`LES`、`RST`；
- 目录改名不等于修改 Java 类名。STAR-CCM+ 宏入口仍以 `Code` 中的 Java 类和 README 说明为准；
- 如果仅凭原目录名无法准确判断功能，使用 Java 类名和 README 中的用途作为命名依据，并在后续人工确认后再细化。

## 一个功能目录应该包含什么

每个独立功能或案例目录至少包含：

```text
功能名称/
├─ Code/          # 该功能或案例自己的 Java 文件
├─ README.md      # 面向普通用户的用途、运行方法和注意事项
└─ AGENTS.md      # 面向维护者的范围、Input、Output、Workflow 规则
```

只有脚本实际需要 `.sim`、CSV、几何文件、模板或其他输入时，才创建 `Input_Files`。不创建空的输入目录，也不把输出结果、缓存和测试产物当作参考代码保存。

如果一个案例由多个 Java 文件组成，应在同一个案例目录的 `README.md` 中说明：

- 每个 Java 文件的职责；
- 文件之间是执行步骤、辅助宏还是不同工况；
- 正确的运行顺序；
- 输入文件和输出结果；
- 是否存在共享库依赖。

多个 Java 文件不一定代表多个项目。判断依据是代码中的调用关系、共同输入、共同输出、命名关联和运行顺序，而不是文件数量。

## 使用方式

1. 先进入与目标功能对应的独立功能目录；
2. 阅读该目录的 `README.md`，确认 STAR-CCM+ 版本、输入文件、运行顺序和输出；
3. 在 STAR-CCM+ 中打开所需的 simulation；
4. 通过宏管理器加载 `Code` 中的 Java 文件；
5. 按 README 中规定的顺序执行，并检查输出对象、报告、场景或导出文件。

本目录中的代码通常不能脱离 STAR-CCM+ 的 Java API 环境直接用普通 `javac` 编译。具体兼容性以本机安装版本和官方文档为准。

## 来源和版本

本目录是经过筛选和重组的参考资料，不是 GitHub 仓库的原样镜像。README 中只在必要时注明原始项目来源；目录结构以“一个独立功能或案例”为单位，而不是以 GitHub 仓库为单位。

- 同一项目的历史版本只保留当前可用版本；
- 测试、重复和临时尝试不进入活动目录；
- 旧版本和整理过程中的原始材料保留在 `98_Previous_Organization`；
- Optimate 的完整 `003_Project` 已整体保存在 `External_STARCCM_Runners\Optimate_Macro_Library`，没有拆散到本目录。
