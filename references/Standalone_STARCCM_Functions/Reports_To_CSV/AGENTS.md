# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 ReportsToCSV.execute()，并调用 execute(), getActiveSimulation(), setFileSelectionMode(), getSelectedFile(), getPresentationName(), getPath(), getProperty(), resolvePath() 操作 Liquid、StarMacro、Simulation、Report、Units、PhysicsContinuum，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：File Name、file.separator、.csv、Simulation Name:、Report Name,     Value,        Unit,   \n、kg/s、bar、Physics 1。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：Liquid、StarMacro、Simulation、Report、Units、PhysicsContinuum。
- 代码执行的结果操作：execute()、setFileSelectionMode()、write()。

## Workflow

```mermaid
flowchart TD
    F1["ReportsToCSV.java / ReportsToCSV.execute(): 文件入口"]
    F1 --> F1M["方法链：execute()"]
    F1M --> F1A["实际调用：execute(), getActiveSimulation(), setFileSelectionMode(), getSelectedFile(), getPresentationName(), getPath(), getProperty(), resolvePath(), write(), getReportManager(), ge..."]
    F1A --> F1O["对象/API 类型：Liquid、StarMacro、Simulation、Report、Units、PhysicsContinuum"]
    F1O --> F1S["状态变化：execute()、setFileSelectionMode()、write()"]
    F1S --> F1C1113282330{"控制流：try"}
    F1C1113282330 --> F1C1787158801{"控制流：for (Report thisReport : reportCollection)"}
    F1C1787158801 --> F1C1654520803{"控制流：if(thisReport.getUnits() == unit_1)"}
    F1C1654520803 --> F1C987873556{"控制流：for(NamedObject n : no)"}
    F1C987873556 --> F1C112125663{"控制流：catch (IOException iOException) {"}
    F1C112125663 --> F1R["名称/路径/参数：File Name、file.separator、.csv、Simulation Name:、Report Name, Value, Unit, \n、kg/s、bar、Physics 1"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
