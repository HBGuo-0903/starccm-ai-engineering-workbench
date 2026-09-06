# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 excel.execute()，并调用 execute(), execute0(), getActiveSimulation(), getSessionDir(), createSheet(), createRow(), createCell(), setCellValue() 操作 StarMacro、Simulation，实现读取或转换外部数据并写入 simulation 设置，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：BravoI、2016-1027-014、prop_data_、.xls、gc_data_、Model、Speed (mph)、Trim (deg)。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation。
- 代码执行的结果操作：execute()、execute0()、createSheet()、createRow()、createCell()、setCellValue()、write()、close()、addValue()。

## Workflow

```mermaid
flowchart TD
    F1["excel.java / excel.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → execute0()"]
    F1M --> F1A["实际调用：execute(), execute0(), getActiveSimulation(), getSessionDir(), createSheet(), createRow(), createCell(), setCellValue(), write(), close(), readAll(), getRoot()"]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation"]
    F1O --> F1S["状态变化：execute()、execute0()、createSheet()、createRow()、createCell()、setCellValue()、write()、close()、addValue()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C449987368{"控制流：for (rowIterator = data.size() - 1; rowIterator >= data.size() - numToAve; rowIterator--) {"}
    F1C449987368 --> F1C852018339{"控制流：if (columnIterator == 12 || columnIterator == 16) {"}
    F1C852018339 --> F1R["名称/路径/参数：BravoI、2016-1027-014、prop_data_、.xls、gc_data_、Model、Speed (mph)、Trim (deg)"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
