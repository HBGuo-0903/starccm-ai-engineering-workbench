# Colormaps

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo10_Colormaps.execute()，并调用 execute(), getActiveSimulation(), cameraView(), addColormaps(), saveSim(), all(), add(), clear() 操作 MacroUtils、UserDeclarations、StarMacro，实现配置场景、视图和可视化对象以生成可检查的结果，解决场景显示和可视化输出依赖重复手工设置。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo10_Colormaps、cam|7.698946e-03,-2.109472e-02,7.961378e-02、|7.698946e-03,-2.109472e-02,6.679604e-01|0.000000e+00,1.000000e+00,0.000000e+00、|1.288606e-01|1、myColormap、myColormap2、Sphere、Centroid。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo10_Colormaps.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro。
- 代码执行的结果操作：execute()、addColormaps()、saveSim()、add()、clear()、setPresentationName()、addAll()、setDisplayMeshBoolean()、open()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
