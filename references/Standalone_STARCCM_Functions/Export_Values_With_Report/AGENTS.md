# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 exportValuesWithReport.execute()，并调用 execute(), exportValue(), getActiveSimulation(), getPresentationName(), resolvePath(), getSimulationIterator(), run(), getReportManager() 操作 Report、StarMacro、Simulation，实现将 simulation 中的结果或监视数据整理并导出为可复用文件，解决结果文件和监视数据需要人工整理。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：output.txt、Report 1、Report 2、Report 3、Report 4、ERROR: unidentified report found.、%.1f、\t。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：Report、StarMacro、Simulation。
- 代码执行的结果操作：execute()、exportValue()、run()、write()、close()。

## Workflow

```mermaid
flowchart TD
    F1["exportValuesWithReport.java / exportValuesWithReport.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → exportValue() → run()"]
    F1M --> F1A["实际调用：execute(), exportValue(), getActiveSimulation(), getPresentationName(), resolvePath(), getSimulationIterator(), run(), getReportManager(), getObjects(), getReportMonitorVal..."]
    F1A --> F1O["对象/API 类型：Report、StarMacro、Simulation"]
    F1O --> F1S["状态变化：execute()、exportValue()、run()、write()、close()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C1009776735{"控制流：for (Report rep: reports) {"}
    F1C1009776735 --> F1C1687637464{"控制流：if (rep instanceof HeatTransferReport) {"}
    F1C1687637464 --> F1C1422047707{"控制流：if (rep.getPresentationName().contains('Report 1')) {"}
    F1C1422047707 --> F1C378235184{"控制流：for (double value: reportValues){"}
    F1C378235184 --> F1R["名称/路径/参数：output.txt、Report 1、Report 2、Report 3、Report 4、ERROR: unidentified report found.、%.1f、\t"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
