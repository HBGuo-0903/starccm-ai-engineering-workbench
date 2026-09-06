# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 PCoeff_3D_Scene.execute()，并调用 execute(), getActiveSimulation(), initMacro(), createAndExportScenes(), getSimulation(), getFieldFunctionManager(), getFunction(), getReferenceVelocity() 操作 StarMacro、Simulation、MacroUtils、UserDeclarations，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：BravoI、Fury4_6036、62.7mph_10.0deg_7.19in_3396.0rpm、\\\\MMFDLHPCP01\\scratch\\Gunderson\\CFD_TRs\\TR2016-prop\\star\\、\\、.sim、PressureCoefficient、Blade1|Blades|Hub|Strut。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、MacroUtils、UserDeclarations。
- 代码执行的结果操作：execute()、createAndExportScenes()、setValue()、addAll()、setPresentationName()、setClip()、setRange()、export3DSceneFileAndWait()。

## Workflow

```mermaid
flowchart TD
    F1["PCoeff_3D_Scene.java / PCoeff_3D_Scene.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → createAndExportScenes()"]
    F1M --> F1A["实际调用：execute(), getActiveSimulation(), initMacro(), createAndExportScenes(), getSimulation(), getFieldFunctionManager(), getFunction(), getReferenceVelocity(), setValue(), addAl..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、MacroUtils、UserDeclarations"]
    F1O --> F1S["状态变化：execute()、createAndExportScenes()、setValue()、addAll()、setPresentationName()、setClip()、setRange()、export3DSceneFileAndWait()"]
    F1S --> F1C1351972364{"控制流：for (String folder : propModels) {"}
    F1C1351972364 --> F1C1154056253{"控制流：for (String state : runState) {"}
    F1C1154056253 --> F1R["名称/路径/参数：BravoI、Fury4_6036、62.7mph_10.0deg_7.19in_3396.0rpm、\\\\MMFDLHPCP01\\scratch\\Gunderson\\CFD_TRs\\TR2016-prop\\star\\、\\、.sim、PressureCoefficient、Blade1|Blades|Hub|Strut"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
