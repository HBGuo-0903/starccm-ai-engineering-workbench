# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 SetRPM.execute()，并调用 execute(), setRPM(), saveAs(), getActiveSimulation(), get(), createGlobalParameter(), setDimensions(), Builder() 操作 Simulation、StarMacro、Units，实现配置 simulation 的物理模型、边界条件或求解参数，解决物理模型、边界或求解参数容易配置不一致。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Base_1140RPM.sim、RPM、rpm。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：Simulation、StarMacro、Units。
- 代码执行的结果操作：execute()、setRPM()、saveAs()、createGlobalParameter()、setDimensions()、setUnits()、setValue()、saveState()。

## Workflow

```mermaid
flowchart TD
    F1["SetRPM.java / SetRPM.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → setRPM() → saveAs()"]
    F1M --> F1A["实际调用：execute(), setRPM(), saveAs(), getActiveSimulation(), get(), createGlobalParameter(), setDimensions(), Builder(), build(), getUnitsManager(), getObject(), getQuantity()"]
    F1A --> F1O["对象/API 类型：Simulation、StarMacro、Units"]
    F1O --> F1S["状态变化：execute()、setRPM()、saveAs()、createGlobalParameter()、setDimensions()、setUnits()、setValue()、saveState()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1R["名称/路径/参数：Base_1140RPM.sim、RPM、rpm"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
