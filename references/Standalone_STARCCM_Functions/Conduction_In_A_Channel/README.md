# Conduction_In_A_Channel

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo2_Conduction_In_a_Channel.execute()，并调用 execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), setPresentationName() 操作 MacroUtils、UserDeclarations、StarMacro、Scene、Mesh、Material、Solver、Plot，实现在 STAR-CCM+ simulation 中执行该功能对应的对象配置和结果处理，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo2_Conduction_In_a_Channel、myCam|5.003809e-01,1.413476e-02,4.204865e-03、|-1.256561e+00,7.844162e-01,1.422375e+00|2.630476e-01,9.462717e-01,-1.880847e-01、|2.280761e-01|1、Channel、.*、x0、x1。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo2_Conduction_In_a_Channel.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro、Scene、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()、addAll()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
