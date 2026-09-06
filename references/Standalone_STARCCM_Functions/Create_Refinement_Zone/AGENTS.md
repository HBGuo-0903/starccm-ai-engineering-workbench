# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 createRefinementZone.execute()，并调用 execute(), CreateVerticalRefinementZone(), getActiveSimulation(), getUnitsManager(), getPreferredUnits(), get(), createNewBlockPart(), setDoNotRetessellate() 操作 StarMacro、Simulation、Units、AutoMeshOperation、MeshOperationManager、Mesh，实现在 simulation 对象树中创建、配置或分配目标对象，解决模型对象需要人工重复创建、配置或分配。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：RefinementZone、[-$StatorDiameter, -$StatorDiameter, 、] 、[$StatorDiameter, $StatorDiameter, 、Automated Mesh。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Units、AutoMeshOperation、MeshOperationManager、Mesh。
- 代码执行的结果操作：execute()、CreateVerticalRefinementZone()、createNewBlockPart()、setDoNotRetessellate()、setCoordinateSystem()、setCoordinate()、setPresentationName()、setDefinition()。

## Workflow

```mermaid
flowchart TD
    F1["createRefinementZone.java / createRefinementZone.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → CreateVerticalRefinementZone()"]
    F1M --> F1A["实际调用：execute(), CreateVerticalRefinementZone(), getActiveSimulation(), getUnitsManager(), getPreferredUnits(), get(), createNewBlockPart(), setDoNotRetessellate(), getCoordinate..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Units、AutoMeshOperation、MeshOperationManager、Mesh"]
    F1O --> F1S["状态变化：execute()、CreateVerticalRefinementZone()、createNewBlockPart()、setDoNotRetessellate()、setCoordinateSystem()、setCoordinate()、setPresentationName()、setDefinition()"]
    F1S --> F1R["名称/路径/参数：RefinementZone、[-$StatorDiameter, -$StatorDiameter, 、] 、[$StatorDiameter, $StatorDiameter, 、Automated Mesh"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
