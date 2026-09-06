# HPC STAR-CCM+ Scheduler

这是一个使用 Slurm 调度器在 HPC 环境中运行 STAR-CCM+ 和 Design Manager 的参考项目。

## 使用前

- 准备 `Input_Files\rocket_example.sim`。
- 修改 `Documentation\run_sim.txt`、`run_sim_macro.txt` 或 `runDM.txt` 中的账户、分区、资源、`STAR_PATH`、`SIM_PATH` 和 `MACRO_PATH`。
- 确认集群已安装 Slurm、STAR-CCM+ 和所需 Java 宏。

## 运行关系

使用 `sbatch` 提交作业，Slurm 分配资源后调用 `STAR_PATH/starccm+` 的批处理或 Design Manager 参数。完成后同时检查 Slurm 状态、STAR-CCM+ 日志、退出码和 simulation 结果。

## 参考来源

本目录是开源 STAR-CCM+ HPC 调度示例的功能化参考副本；原始来源记录见同级 `99_Original_Repositories`。
