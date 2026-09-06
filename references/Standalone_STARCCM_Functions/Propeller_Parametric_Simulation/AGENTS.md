# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Props.execute()，并调用 execute(), initMacro(), setSpeed(), setHeight(), setTrim(), setCsys(), setRpm(), run() 操作 Displayer、StarMacro、Simulation、MacroUtils、MeshOperationManager、MonitorPlot、Scene、UserDeclarations，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：6036_hub_v、Revision、Speed (mph)、Trim (deg)、Height (in.)、RPM、Prop Lift (lbf)、Prop Sideforce (lbf)。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：Displayer、StarMacro、Simulation、MacroUtils、MeshOperationManager、MonitorPlot、Scene、UserDeclarations。
- 代码执行的结果操作：execute()、setSpeed()、setHeight()、setTrim()、setCsys()、setRpm()、run()、exportScene()、CreateResultSS()。

## Workflow

```mermaid
flowchart TD
    F1["Props.java / Props.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → setSpeed() → setHeight() → setTrim() → setCsys() → setRpm() → run() → exportScene()"]
    F1M --> F1A["实际调用：execute(), initMacro(), setSpeed(), setHeight(), setTrim(), setCsys(), setRpm(), run(), exportScene(), CreateResultSS(), getSimulation(), getActiveSimulation()"]
    F1A --> F1O["对象/API 类型：Displayer、StarMacro、Simulation、MacroUtils、MeshOperationManager、MonitorPlot、Scene、UserDeclarations"]
    F1O --> F1S["状态变化：execute()、setSpeed()、setHeight()、setTrim()、setCsys()、setRpm()、run()、exportScene()、CreateResultSS()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C1787382376{"控制流：for (double speed : speeds) {"}
    F1C1787382376 --> F1C553473132{"控制流：for (double height : heights) {"}
    F1C553473132 --> F1C271160493{"控制流：for (double trim : trims) {"}
    F1C271160493 --> F1C1432622892{"控制流：for (double rpm : rpms) {"}
    F1C1432622892 --> F1R["名称/路径/参数：6036_hub_v、Revision、Speed (mph)、Trim (deg)、Height (in.)、RPM、Prop Lift (lbf)、Prop Sideforce (lbf)"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
