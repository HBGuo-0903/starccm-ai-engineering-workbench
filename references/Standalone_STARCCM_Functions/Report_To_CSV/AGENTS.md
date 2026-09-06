# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Report_to_CSV.execute()，并调用 execute(), getActiveSimulation(), getPresentationName(), resolvePath(), write(), getReportManager(), getObjects(), getReportMonitorValue() 操作 StarMacro、Simulation、Report、Units，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Simulation Name:、<sim_file_name>+report.csv、report.csv、Report Name, Value, Unit, \n、Field Location :、 Field Value :、 Field Units :、sim file name。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Report、Units。
- 代码执行的结果操作：execute()、write()、close()。

## Workflow

```mermaid
flowchart TD
    F1["Report_to_CSV.java / Report_to_CSV.execute(): 文件入口"]
    F1 --> F1M["方法链：execute()"]
    F1M --> F1A["实际调用：execute(), getActiveSimulation(), getPresentationName(), resolvePath(), write(), getReportManager(), getObjects(), getReportMonitorValue(), getUnits(), close()"]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Report、Units"]
    F1O --> F1S["状态变化：execute()、write()、close()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C678402082{"控制流：for (Report thisReport : reportCollection){"}
    F1C678402082 --> F1R["名称/路径/参数：Simulation Name:、<sim_file_name>+report.csv、report.csv、Report Name, Value, Unit, \n、Field Location :、 Field Value :、 Field Units :、sim file name"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
