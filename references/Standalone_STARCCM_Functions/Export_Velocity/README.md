# Export_Velocity

## 用途

本宏通过在 STAR-CCM+ 中执行 ExportVelocity.execute()，并调用 execute(), getActiveSimulation(), createVelocityVectorScene(), runTime(), createEmptyVectorScene(), getScene(), createSubFolder(), getDisplayerManager() 操作 StarMacro、Simulation、Scene、Legend、FieldFunction、Displayer、Plane、CurrentView，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Instantaneous Velocity、images、Velocity、blue-red、%-#6.2f、Maximum Physical Time、XZ Plane、Outline。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\ExportVelocity.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Scene、Legend、FieldFunction、Displayer、Plane、CurrentView。
- 代码执行的结果操作：execute()、createVelocityVectorScene()、runTime()、createEmptyVectorScene()、createSubFolder()、initialize()、addFieldFunctionToVectorDisplayer()、addPartsToDisplayer()、setVectorDisplayerToTangential()、setVectorDisplayerToLIC()、setDisplayMode()、setConstrain()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
