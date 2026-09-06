# Directed_Meshing

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo11_Directed_Meshing.execute()，并调用 execute(), initMacro(), post(), saveSim(), all(), getActiveSimulation(), add(), setPresentationName() 操作 MacroUtils、UserDeclarations、Simulation、StarMacro、AutoMeshOperation、Mesh、Region，实现配置并执行几何或网格处理流程，解决网格流程依赖重复的界面操作且容易漏设。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo11_Directed_Meshing、Cell Surface、Region Index、.*、Scalar|2.579937e-02,2.282902e-02,-8.461343e-03、|2.110428e-01,3.145835e-01,-1.134373e-01|-6.846060e-01,5.891221e-01,4.292433e-01、|7.413223e-02|1、Mesh|4.807398e-02,4.265680e-02,3.831531e-02。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo11_Directed_Meshing.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、Simulation、StarMacro、AutoMeshOperation、Mesh、Region。
- 代码执行的结果操作：execute()、saveSim()、add()、setPresentationName()、setDisplayMeshBoolean()、setVisible()、open()、remove()、addAll()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
