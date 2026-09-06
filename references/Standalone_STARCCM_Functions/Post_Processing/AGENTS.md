# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 post_processing.execute()，并调用 execute(), execute0(), getActiveSimulation(), getPlotManager(), getPlot(), getPlotUpdate(), getHardcopyProperties(), setCurrentResolutionWidth() 操作 StarMacro、Simulation、MonitorPlot、Monitor、Plot、HardcopyProperties、Scene、Legend，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Total Downforce Monitor Plot、Scalar Scene、Outline、Scalar、Scalar Scene 1、Outline 1、Scalar 1、blue-red。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、MonitorPlot、Monitor、Plot、HardcopyProperties、Scene、Legend。
- 代码执行的结果操作：execute()、execute0()、setCurrentResolutionWidth()、setCurrentResolutionHeight()、createScalarScene()、initializeAndWait()、initialize()。

## Workflow

```mermaid
flowchart TD
    F1["post_processing.java / post_processing.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → execute0()"]
    F1M --> F1A["实际调用：execute(), execute0(), getActiveSimulation(), getPlotManager(), getPlot(), getPlotUpdate(), getHardcopyProperties(), setCurrentResolutionWidth(), setCurrentResolutionHeight..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、MonitorPlot、Monitor、Plot、HardcopyProperties、Scene、Legend、SceneUpdate、Region、Boundary、CurrentView"]
    F1O --> F1S["状态变化：execute()、execute0()、setCurrentResolutionWidth()、setCurrentResolutionHeight()、createScalarScene()、initializeAndWait()、initialize()"]
    F1S --> F1R["名称/路径/参数：Total Downforce Monitor Plot、Scalar Scene、Outline、Scalar、Scalar Scene 1、Outline 1、Scalar 1、blue-red"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
