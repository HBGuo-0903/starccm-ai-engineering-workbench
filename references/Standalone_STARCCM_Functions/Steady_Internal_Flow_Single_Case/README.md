# Steady_Internal_Flow_Single_Case

## 用途

本宏通过在 STAR-CCM+ 中执行 SS_Internal_Single.execute()，并调用 execute(), getSimulation(), getPartManager(), getObjects(), initMacro(), physics(), mesh(), monitors() 操作 StarMacro、MacroUtils、Material、Solver、Part、Report、UserDeclarations，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：v10、23Lpm、Revision、, 、water、.x_b、.*、Velocity Contours。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\SS_Internal_Single.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、MacroUtils、Material、Solver、Part、Report、UserDeclarations。
- 代码执行的结果操作：execute()、saveSim()、generic()、importPart()、setAutoSaveMesh()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
