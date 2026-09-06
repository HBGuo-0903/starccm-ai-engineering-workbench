# Run_DES_Simulation

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo15_Run_DES.execute()，并调用 execute(), initMacro(), runRANS(), runDES(), saveSim(), all(), getPresentationName(), setDisplayMeshBoolean() 操作 MacroUtils、UserDeclarations、Monitor、StarMacro、LookupTable、Scene、Mesh、Displayer，实现按预设参数推进 simulation 的网格、初始化和求解过程，解决已有模型无法按固定顺序重复运行并保存结果。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Polys And Prisms、Fixing Scalar Displayer for Scene: 、Scalar.*、Demo15_Run_DES、cam1|5.530468e-02,-1.190976e-02,-7.199782e-02、|4.282329e-01,2.739476e-01,5.776558e-01|-1.501948e-01,9.338231e-01,-3.246781e-01、|8.359997e-02|1、cam2|1.841383e-02,2.223453e-02,-3.292230e-05。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo15_Run_DES.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、Monitor、StarMacro、LookupTable、Scene、Mesh、Displayer。
- 代码执行的结果操作：execute()、runRANS()、runDES()、saveSim()、setDisplayMeshBoolean()、setLabelFormat()、setLookupTable()、setNumberOfLabels()、setAutoRange()、setRange()、setWidth()、setPositionCoordinate()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
