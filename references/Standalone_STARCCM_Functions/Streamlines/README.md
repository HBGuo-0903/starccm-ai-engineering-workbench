# Streamlines

## 用途

本宏通过在 STAR-CCM+ 中执行 Demo13_Streamlines.execute()，并调用 pictures(), execute(), initMacro(), post(), saveSim(), all(), getActiveSimulation(), cameraView() 操作 MacroUtils、UserDeclarations、Simulation、StarMacro、Mesh、Part、Displayer、Material，实现配置场景、视图和可视化对象以生成可检查的结果，解决场景显示和可视化输出依赖重复手工设置。

## 前置条件和输入

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo13_Streamlines、cam1|2.234138e-03,-5.793002e-04,3.969814e-02、|2.234138e-03,-5.793002e-04,3.269352e-01|0.000000e+00,1.000000e+00,0.000000e+00、|7.498392e-02|0、cam2|2.873504e-04,3.784037e-03,3.752940e-02、|2.873504e-04,3.784037e-03,2.115639e-01|0.000000e+00,1.000000e+00,0.000000e+00、|4.543212e-02|0、Scalar。运行前需确认模型树中名称一致。

## 运行方法

- 在 STAR-CCM+ 中打开目标 simulation，并确认输入中的对象名称、文件路径和版本要求。
- 在 STAR-CCM+ 中加载并执行 Code\Demo13_Streamlines.java 的 execute()。
- 执行完成后，按“输出”部分检查对象、求解状态和结果文件。

## 输出

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、Simulation、StarMacro、Mesh、Part、Displayer、Material。
- 代码执行的结果操作：execute()、saveSim()、addAll()、setAxesVisible()、setOpacity()、setVisible()、setColorMode()、setMesh()、setPresentationName()。

## 参考来源

本目录是开源 STAR-CCM+ Java 宏的功能化参考副本；原始仓库和历史文件见同级 99_Original_Repositories 与 98_Previous_Organization。
