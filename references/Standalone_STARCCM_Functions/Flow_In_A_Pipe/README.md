# Flow_In_A_Pipe

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo1_Flow_In_a_Pipe.execute()，并调用 execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), byREGEX() 操作 MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo1_Flow_In_a_Pipe、myCam|2.498938e-01,-1.450833e-02,-2.222717e-02、|2.498938e-01,-1.450833e-02,9.690091e-01|0.000000e+00,1.000000e+00,0.000000e+00、|1.463440e-01|1、x0、x1、Def.*、My Mesh。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo1_Flow_In_a_Pipe.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
