# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 ExporterV2.execute()，并调用 execute(), getActiveSimulation(), getProperty(), getSessionDir(), getPresentationName(), getAbsolutePath(), getPlots(), createScenes() 操作 StarMacro、Simulation、Scene、Units、FieldFunction、CurrentView、Plot、CoordinateSystem，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：file.separator、 Copy、Simulation Directory created: 、Scenes、 .jpg、 .sce、Velocity、PressureCoefficient。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Scene、Units、FieldFunction、CurrentView、Plot、CoordinateSystem。
- 代码执行的结果操作：execute()、createScenes()、exportScene()、export3DSceneFileAndWait()、addAll()。

## Workflow

```mermaid
flowchart TD
    F1["ExporterV2.java / ExporterV2.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → exportScene() → createScenes() → getPlots() → exportSweep() → scenePresentation() → exportYSweep() → exportXSweep() → exportZSweep() → fuckKnowsFunction()"]
    F1M --> F1A["实际调用：execute(), getActiveSimulation(), getProperty(), getSessionDir(), getPresentationName(), getAbsolutePath(), getPlots(), createScenes(), exportScene(), printAndWait(), resol..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Scene、Units、FieldFunction、CurrentView、Plot、CoordinateSystem、Region、Part"]
    F1O --> F1S["状态变化：execute()、createScenes()、exportScene()、export3DSceneFileAndWait()、addAll()"]
    F1S --> F1C1255262800{"控制流：while (!dir.mkdir()) {"}
    F1C1255262800 --> F1C2102394374{"控制流：for (String name : functionNames) {"}
    F1C2102394374 --> F1C20904463{"控制流：if (name.equals('Velocity')) {"}
    F1C20904463 --> F1C2137224650{"控制流：if (dir.mkdir()) {"}
    F1C2137224650 --> F1C1822383597{"控制流：for (StarPlot plot : simulation.getPlotManager().getObjects()) {"}
    F1C1822383597 --> F1R["名称/路径/参数：file.separator、 Copy、Simulation Directory created: 、Scenes、 .jpg、 .sce、Velocity、PressureCoefficient"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
