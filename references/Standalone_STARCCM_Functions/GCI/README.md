# GCI

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo14_GCI.execute()，并调用 execute(), initMacro(), saveSim(), all(), byREGEX(), getSideA(), getSideB(), getActiveSimulation() 操作 MacroUtils、UserDeclarations、FieldFunction、StarMacro、Mesh、Material、Solver、Plot，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Numerical vs Analytical Solutions、Vmax、Vmean、z0、z1、Demo14_GCI、cam1|-2.733933e-04,-2.870785e-04,2.535976e-03、|9.205652e-02,1.539672e-02,1.080102e-01|1.614315e-02,9.868431e-01,-1.608729e-01。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo14_GCI.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、FieldFunction、StarMacro、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、saveSim()、setupPhysics()、setupRegion()、setupMesh()、setupBCs()、setupPost()、setSelected()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
