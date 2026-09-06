# Solution_History_And_Cameras

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo12_Solution_History_And_Cameras.execute()，并调用 execute(), initMacro(), postTRN(), saveSim(), all(), getActiveSimulation(), cameraView(), postSS() 操作 MacroUtils、UserDeclarations、Boundary、Simulation、StarMacro、AutoMeshOperation、Material、Solver，实现建立并保存用于监测或后处理的历史数据，解决监测历史和后处理数据准备过程容易不一致。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo12_Solution_History_And_Cameras、cam1|1.516797e-01,-4.188893e-03,-6.132604e-03、|1.516797e-01,-4.188893e-03,1.101325e+00|0.000000e+00,1.000000e+00,0.000000e+00、|1.165985e-01|1、cam2|5.502414e-02,3.902467e-04,-1.586686e-04、|5.502414e-02,3.902467e-04,1.101325e+00|0.000000e+00,1.000000e+00,0.000000e+00、|4.309526e-02|1、cam3|3.000000e-01,3.902467e-04,-1.586686e-04。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo12_Solution_History_And_Cameras.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、Boundary、Simulation、StarMacro、AutoMeshOperation、Material、Solver。
- 代码执行的结果操作：execute()、saveSim()、updateVariables()、add()、setLabelFormat()、setClip()、setRange()、setRepresentation()、setDisplayMeshBoolean()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
