# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Jpo_Boat_Para_SinglePhase.execute()，并调用 execute(), initMacro(), post(), getSimulation(), getActiveSimulation(), plots(), byREGEX(), picture() 操作 StarMacro、MacroUtils、UserDeclarations，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：310slx_hydro、_sink、_roll、_pitch、_yaw、_speed、waterline、data。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、MacroUtils、UserDeclarations。
- 代码执行的结果操作：execute()、createSheet()、createRow()、createCell()、setCellValue()、write()、close()、create()。

## Workflow

```mermaid
flowchart TD
    F1["Jpo_Boat_Para_SinglePhase.java / Jpo_Boat_Para_SinglePhase.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro()"]
    F1M --> F1A["实际调用：execute(), initMacro(), post(), getSimulation(), getActiveSimulation(), plots(), byREGEX(), picture(), createSheet(), createRow(), createCell(), setCellValue()"]
    F1A --> F1O["对象/API 类型：StarMacro、MacroUtils、UserDeclarations"]
    F1O --> F1S["状态变化：execute()、createSheet()、createRow()、createCell()、setCellValue()、write()、close()、create()"]
    F1S --> F1C1319833049{"控制流：for (double sink : sinks) {"}
    F1C1319833049 --> F1C1760884239{"控制流：for (double pitch : pitches) {"}
    F1C1760884239 --> F1C1253696103{"控制流：for (double yaw : yaws) {"}
    F1C1253696103 --> F1C63850940{"控制流：if (yaw == 0.) {"}
    F1C63850940 --> F1C1787382376{"控制流：for (double speed : speeds) {"}
    F1C1787382376 --> F1R["名称/路径/参数：310slx_hydro、_sink、_roll、_pitch、_yaw、_speed、waterline、data"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
