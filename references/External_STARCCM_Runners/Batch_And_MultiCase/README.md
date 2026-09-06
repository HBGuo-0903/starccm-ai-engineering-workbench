# Batch and Multi-Case Runner

这是一个从外部生成 STAR-CCM+ 批处理命令并执行多阶段案例的参考项目。Python 驱动器负责组织网格阶段、求解阶段、许可证参数和 simulation 文件传递，Java 文件负责 STAR-CCM+ 内部宏或数据转换。

## 使用前

- 准备 `Input_Files\combined.stl` 和 `Input_Files\config.ini`。
- 确认 STAR-CCM+ 可执行程序、许可证路径和 `STAR_POWER_ON_DEMAND_LIC`。
- 根据实际环境修改脚本中的 Linux/C-Shell 路径和宏文件名。

## 运行关系

先由 `Code\StarCCM.py` 生成网格阶段命令，网格完成后把 simulation 传递给求解阶段，再检查 `starccmMeshRun.out`、`starccmFlowRun.out`、CSV 和 PNG。测试文件及文件碰撞副本已放入历史归档。

## 参考来源

本目录是开源 STAR-CCM+ 批处理项目的功能化参考副本；原始来源记录见同级 `99_Original_Repositories`。
