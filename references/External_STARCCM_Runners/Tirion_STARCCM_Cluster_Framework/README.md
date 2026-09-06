# Tirion STAR-CCM+ Cluster Framework

这是一个用于集群批处理的 STAR-CCM+ 运行框架。用户通过 `Launcher\tirion.py` 指定作业参数和 `.sim` 文件，框架生成 Slurm 作业脚本并启动 STAR-CCM+，再选择网格、完整运行或后处理宏。

## 主要入口

- `Launcher\tirion.py`：接收作业名、仿真路径、节点数、核心数和运行模式。
- `Launcher\tirion.sh`：集群提交模板。
- `Code\src\main\MainMacro.java`：主要运行宏。
- `Code\src\main\FullCore.java`：完整流程宏。
- `Code\src\main\PostProcessing.java`：后处理宏。

该项目依赖 Slurm 和原始集群环境。迁移到本地 Windows 后不能直接运行，使用前必须重新配置 STAR-CCM+ 路径、集群资源和工作目录。

`Code\src\deprecated` 中的宏仅作历史参考，不属于默认运行入口；`Launcher` 与 `Code` 必须作为同一个集群项目整体保留。
