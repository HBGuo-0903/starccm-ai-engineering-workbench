# STAR-CCM+ External Result Extraction

这是一个由 Python、MATLAB 和 Java 宏组成的外部结果提取项目，用于批量读取 STAR-CCM+ 报告、压力系数、翼载荷和场景数据，并生成 CSV 和图表。

## 使用前

- 准备 `Input_Files\bend_tube.sim` 和 `Input_Files\batch_config.json`。
- 配置 STAR-CCM+、Python 和 MATLAB 路径。
- 确认 `Report_to_csv.java`、`WingLoadBatch.java` 等宏与 simulation 中的报告和对象名称一致。

## 运行关系

由 `main.py`、`star_runner.py` 或 MATLAB 脚本启动 STAR-CCM+ 批处理宏，再由 MATLAB/Python 读取导出的 CSV 和场景结果。成功判据是进程、宏、CSV、图表和日志均可验证。

## 参考来源

本目录是开源 STAR-CCM+ 结果提取项目的功能化参考副本；原始来源记录见同级 `99_Original_Repositories`。
