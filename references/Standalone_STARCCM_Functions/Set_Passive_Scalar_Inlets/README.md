# Set_Passive_Scalar_Inlets

## 用途

本宏通过在 STAR-CCM+ 中执行 SetPassiveScalarInlets.execute()，并调用 execute(), SetPassiveScalarInlet(), getActiveSimulation(), getRegionManager(), getRegion(), getBoundaryManager(), getBoundary(), getValues() 操作 StarMacro、Simulation、Region、Boundary，实现配置 simulation 的物理模型、边界条件或求解参数，解决物理模型、边界或求解参数容易配置不一致。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：yBa_2+、ySO4_2-、yNa_1+、yCl_1-、InletA、InletB、Fluid。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\SetPassiveScalarInlets.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Region、Boundary。
- 代码执行的结果操作：execute()、SetPassiveScalarInlet()、setMethod()、setValue()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
