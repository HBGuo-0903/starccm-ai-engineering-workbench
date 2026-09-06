# Normals2

## 用途

本宏通过在 STAR-CCM+ 中执行 Normals2.execute()，并调用 execute(), execute0(), getActiveSimulation(), getPartManager(), getObject(), getRepresentationManager(), exportDataSourceSTL(), resolvePath() 操作 StarMacro、Simulation、FvRepresentation、Mesh、Scene、CurrentView，实现配置场景、视图和可视化对象以生成可检查的结果，解决场景显示和可视化输出依赖重复手工设置。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Y Normal、Volume Mesh、N:\\1 - USM23 CAD\\Cooling\\CFD\\Radiator\\Old Sims\\YNormal.stl、Turbulent Kinetic Energy。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Normals2.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、FvRepresentation、Mesh、Scene、CurrentView。
- 代码执行的结果操作：execute()、execute0()、exportDataSourceSTL()、setInput()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
