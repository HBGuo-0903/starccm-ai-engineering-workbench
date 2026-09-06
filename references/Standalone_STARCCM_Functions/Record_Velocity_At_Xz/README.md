# Record_Velocity_At_Xz

## 用途

本宏通过在 STAR-CCM+ 中执行 RecordVelocityAtXz.execute()，并调用 execute(), Builder(), build(), createScalarFieldFunction(), createSolutionHistory(), runOneRev(), SaveFile(), getActiveSimulation() 操作 StarMacro、Simulation、UserFieldFunction、Units、FieldFunction、Plane、Region，实现建立并保存用于监测或后处理的历史数据，解决监测历史和后处理数据准备过程容易不一致。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：, 、Sim_、TurbStats、 + uid + 、XZ Plane、Fluid、Mean 、Maximum Physical Time。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\RecordVelocityAtXz.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、UserFieldFunction、Units、FieldFunction、Plane、Region。
- 代码执行的结果操作：execute()、createScalarFieldFunction()、createSolutionHistory()、runOneRev()、SaveFile()、Save()、createDirectories()、saveState()、RunSimulation()、run()、createFieldFunction()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
