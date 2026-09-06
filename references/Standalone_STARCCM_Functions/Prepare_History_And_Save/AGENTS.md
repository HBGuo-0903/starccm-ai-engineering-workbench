# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 PrepareHistoryAndSave.execute()，并调用 execute(), setInnerIterationCount(), enableSecondOrderTimestep(), getActiveRootObject(), resolvePath(), Save(), getActiveSimulation(), getSessionDirFile() 操作 StarMacro、Simulation，实现建立并保存用于监测或后处理的历史数据，解决监测历史和后处理数据准备过程容易不一致。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：CreateLineProbe.java、RecordReynoldsStresses.java、CreateExportTable.java、RunOneRev.java、.sim、Maximum Inner Iterations。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation。
- 代码执行的结果操作：execute()、setInnerIterationCount()、enableSecondOrderTimestep()、Save()、saveState()、setMaximumNumberInnerIterations()、setSelected()。

## Workflow

```mermaid
flowchart TD
    F1["PrepareHistoryAndSave.java / PrepareHistoryAndSave.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → Save() → setInnerIterationCount() → enableSecondOrderTimestep()"]
    F1M --> F1A["实际调用：execute(), setInnerIterationCount(), enableSecondOrderTimestep(), getActiveRootObject(), resolvePath(), Save(), getActiveSimulation(), getSessionDirFile(), getName(), saveS..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation"]
    F1O --> F1S["状态变化：execute()、setInnerIterationCount()、enableSecondOrderTimestep()、Save()、saveState()、setMaximumNumberInnerIterations()、setSelected()"]
    F1S --> F1R["名称/路径/参数：CreateLineProbe.java、RecordReynoldsStresses.java、CreateExportTable.java、RunOneRev.java、.sim、Maximum Inner Iterations"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
