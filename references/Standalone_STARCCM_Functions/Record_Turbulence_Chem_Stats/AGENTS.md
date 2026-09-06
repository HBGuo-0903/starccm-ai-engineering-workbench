# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 RecordTurbulenceChemStats.execute()，并调用 execute(), Builder(), build(), createMeanScalarMonitor(), createVectorFieldFunction(), createMeanFlowMonitor(), createTensorFieldFunction(), createScalarFieldFunction() 操作 StarMacro、Monitor、Simulation、UserFieldFunction、Units、Region、FieldFunction，实现建立并保存用于监测或后处理的历史数据，解决监测历史和后处理数据准备过程容易不一致。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：mNa_1+、c_u_i、[$${Velocity}[0] * ${mNa_1+}, $${Velocity}[1] * ${mNa_1+}, $${Velocity}[2] * ${mNa_1+}]、Velocity ii、vel_ii、Velocity ij、vel_ij、Velocity。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Monitor、Simulation、UserFieldFunction、Units、Region、FieldFunction。
- 代码执行的结果操作：execute()、createMeanScalarMonitor()、createVectorFieldFunction()、createMeanFlowMonitor()、createTensorFieldFunction()、createScalarFieldFunction()、runOneRev()、SaveFile()、Save()、createDirectories()、saveState()、RunSimulation()。

## Workflow

```mermaid
flowchart TD
    F1["RecordTurbulenceChemStats.java / RecordTurbulenceChemStats.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → SaveFile() → Save() → RunSimulation() → createScalarFieldFunction() → createVectorFieldFunction() → createTensorFieldFunction() → createMeanFlowMonitor() → creat..."]
    F1M --> F1A["实际调用：execute(), Builder(), build(), createMeanScalarMonitor(), createVectorFieldFunction(), createMeanFlowMonitor(), createTensorFieldFunction(), createScalarFieldFunction(), ru..."]
    F1A --> F1O["对象/API 类型：StarMacro、Monitor、Simulation、UserFieldFunction、Units、Region、FieldFunction"]
    F1O --> F1S["状态变化：execute()、createMeanScalarMonitor()、createVectorFieldFunction()、createMeanFlowMonitor()、createTensorFieldFunction()、createScalarFieldFunction()、runOneRev()、SaveFile()、Save(..."]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1R["名称/路径/参数：mNa_1+、c_u_i、[$${Velocity}[0] * ${mNa_1+}, $${Velocity}[1] * ${mNa_1+}, $${Velocity}[2] * ${mNa_1+}]、Velocity ii、vel_ii、Velocity ij、vel_ij、Velocity"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
