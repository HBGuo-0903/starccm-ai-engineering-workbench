# STAR-CCM+ External Automation Driver

这是一个 Python 外部驱动项目，用于发现 STAR-CCM+ 安装、探测版本并通过 `starccm+ -batch` 执行 Java 宏。

## 使用前

- 安装 STAR-CCM+ 并准备目标 `.sim` 和 Java 宏。
- 根据本机目录修改 `Input_Files\compatibility.yaml` 和 `pyproject.toml`。
- 确认 Python 环境及 `driver.py` 所需依赖。

## 运行关系

由 `Code\driver.py` 搜索 `starccm+.bat`，执行版本探测，再调用批处理子进程。运行结束后检查退出码、标准输出、标准错误和 simulation 输出。Smoke Test、负面测试和测试辅助文件已放入历史归档。

## 参考来源

本目录是开源 STAR-CCM+ 外部自动化驱动项目的功能化参考副本；原始来源记录见同级 `99_Original_Repositories`。
