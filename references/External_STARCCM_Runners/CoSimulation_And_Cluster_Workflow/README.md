# CoSimulation and Cluster Workflow

该案例包含 STAR-CCM+ 与外部求解器、集群调度器或操作系统启动器之间的完整运行内容。

本目录中的 `Code` 和 `Launcher` 是一个整体：Java 宏负责 STAR-CCM+ 内部对象与协同仿真设置，外部脚本负责环境准备、作业提交、进程启动和结果收集。不能只拿出 Java 文件在 `Standalone_STARCCM_Functions` 中单独使用。

`Code` 中同时保留 FSI 宏所依赖的辅助类，例如 `ImportCAE.java`、`MeshMorpher.java`、`SolversNode.java`、`ContiuumBuilder.java` 和 `StoppingCriteria.java`。这些文件属于本协同仿真项目的依赖，不再分散到 Standalone 目录。

运行前请检查：

- STAR-CCM+ 和外部求解器的安装路径；
- 集群资源、队列和并行参数；
- `.sim`、Abaqus 或其他输入文件；
- Java 宏中的对象名称、通信端口和映射设置；
- 脚本中的工作目录和绝对路径。

运行结束后应同时检查调度器状态、日志、协同仿真连接和输出文件。

文件名带 `__2`、`__3` 等后缀的完全重复副本、测试文件和确认过的旧版本已移入 `98_Previous_Organization\external_cleanup_20260906\CoSimulation_And_Cluster_Workflow`。仍保留在 `Launcher` 中的同名变体具有不同的节点、核心数或运行参数，不能仅按文件名删除。
