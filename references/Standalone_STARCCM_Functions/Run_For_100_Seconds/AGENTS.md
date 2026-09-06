# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Run100s.execute()，并调用 execute(), run(), RunSimulation(), getActiveSimulation(), getSimulationIterator(), getSolverStoppingCriterionManager(), getSolverStoppingCriterion(), getSolution() 操作 StarMacro、Simulation，实现按预设参数推进 simulation 的网格、初始化和求解过程，解决已有模型无法按固定顺序重复运行并保存结果。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Maximum Physical Time。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation。
- 代码执行的结果操作：execute()、run()、RunSimulation()、setDefinition()。

## Workflow

```mermaid
flowchart TD
    F1["Run100s.java / Run100s.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → RunSimulation() → run()"]
    F1M --> F1A["实际调用：execute(), run(), RunSimulation(), getActiveSimulation(), getSimulationIterator(), getSolverStoppingCriterionManager(), getSolverStoppingCriterion(), getSolution(), getPhys..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation"]
    F1O --> F1S["状态变化：execute()、run()、RunSimulation()、setDefinition()"]
    F1S --> F1R["名称/路径/参数：Maximum Physical Time"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
