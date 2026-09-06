# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 SS_Internal_Single.execute()，并调用 execute(), getSimulation(), getPartManager(), getObjects(), initMacro(), physics(), mesh(), monitors() 操作 StarMacro、MacroUtils、Material、Solver、Part、Report、UserDeclarations，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：v10、23Lpm、Revision、, 、water、.x_b、.*、Velocity Contours。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、MacroUtils、Material、Solver、Part、Report、UserDeclarations。
- 代码执行的结果操作：execute()、saveSim()、generic()、importPart()、setAutoSaveMesh()。

## Workflow

```mermaid
flowchart TD
    F1["SS_Internal_Single.java / SS_Internal_Single.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → physics() → mesh() → monitors() → solve() → post()"]
    F1M --> F1A["实际调用：execute(), getSimulation(), getPartManager(), getObjects(), initMacro(), physics(), mesh(), monitors(), post(), saveSim(), all(), getSimulationIterator()"]
    F1A --> F1O["对象/API 类型：StarMacro、MacroUtils、Material、Solver、Part、Report、UserDeclarations"]
    F1O --> F1S["状态变化：execute()、saveSim()、generic()、importPart()、setAutoSaveMesh()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C280941742{"控制流：if (!mu.check.has.volumeMesh()) {"}
    F1C280941742 --> F1C27573176{"控制流：if (!mu.check.has.solution()) {"}
    F1C27573176 --> F1C1265122835{"控制流：if (!mu.getSimulation().isParallel()) {"}
    F1C1265122835 --> F1C1904564370{"控制流：for (Part ps : sections) {"}
    F1C1904564370 --> F1R["名称/路径/参数：v10、23Lpm、Revision、, 、water、.x_b、.*、Velocity Contours"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
