# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 ExportPlotsScenes.execute()，并调用 execute(), execute0(), getActiveSimulation(), setFileSelectionMode(), getSelectedFile(), getPath(), getProperty(), getPlotManager() 操作 StarMacro、Simulation、Scene、Displayer，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：file.separator、.png、png、scalar、(?i).*scalar.*。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Scene、Displayer。
- 代码执行的结果操作：execute()、execute0()、setFileSelectionMode()。

## Workflow

```mermaid
flowchart TD
    F1["ExportPlotsScenes.java / ExportPlotsScenes.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → execute0()"]
    F1M --> F1A["实际调用：execute(), execute0(), getActiveSimulation(), setFileSelectionMode(), getSelectedFile(), getPath(), getProperty(), getPlotManager(), getObjects(), getPresentationName(), re..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Scene、Displayer"]
    F1O --> F1S["状态变化：execute()、execute0()、setFileSelectionMode()"]
    F1S --> F1C1483560705{"控制流：for(StarPlot plot : sim.getPlotManager().getObjects())"}
    F1C1483560705 --> F1C815350943{"控制流：for(Scene scene : sim.getSceneManager().getObjects())"}
    F1C815350943 --> F1C361046797{"控制流：try{"}
    F1C361046797 --> F1C330002085{"控制流：for(Displayer d : scene.getDisplayerManager().getObjects())"}
    F1C330002085 --> F1C1587097081{"控制流：if(d.getPresentationName().matches('(?i).*scalar.*'))"}
    F1C1587097081 --> F1R["名称/路径/参数：file.separator、.png、png、scalar、(?i).*scalar.*"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
