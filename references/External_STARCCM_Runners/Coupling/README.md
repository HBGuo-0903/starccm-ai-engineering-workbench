# STAR-CCM+ MoorDyn Coupling

这是一个 STAR-CCM+ User Library 与 MoorDyn 动态库耦合的完整参考项目。Java 宏加载 `test.so`，C/C++ 接口负责把 MoorDyn 功能编译为 STAR-CCM+ 可加载的用户库。

## 使用前

- 准备 `Input_Files\PointAbsorber.sim`。
- 根据平台使用 `Documentation\makefile` 或 `makefile_Windows` 构建 `Input_Files\test.so`。
- 确认动态库、STAR-CCM+ 版本和编译器 ABI 匹配。

## 运行关系

先构建动态库，再在 STAR-CCM+ 中执行 `Code\initialize_MoorDyn.java`，最后检查 User Function Manager、耦合函数和 simulation 状态。`test.so` 是运行依赖，不能按测试文件删除。

## 参考来源

本目录是开源 MoorDyn/STAR-CCM+ 耦合项目的功能化参考副本；原始来源记录见同级 `99_Original_Repositories`。
