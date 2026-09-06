# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 MeshAndRun.execute()，并调用 execute(), ExecuteMesh(), RunSimulation(), getActiveSimulation(), getSolution(), initializeSolution(), getSimulationIterator(), run() 操作 StarMacro、Simulation、AutoMeshOperation、MeshOperationManager、Mesh，实现配置并执行几何或网格处理流程，解决网格流程依赖重复的界面操作且容易漏设。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Automated Mesh。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、AutoMeshOperation、MeshOperationManager、Mesh。
- 代码执行的结果操作：execute()、ExecuteMesh()、RunSimulation()、initializeSolution()、run()。

## Workflow

```mermaid
flowchart TD
    F1["MeshAndRun.java / MeshAndRun.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → RunSimulation() → ExecuteMesh() → run()"]
    F1M --> F1A["实际调用：execute(), ExecuteMesh(), RunSimulation(), getActiveSimulation(), getSolution(), initializeSolution(), getSimulationIterator(), run(), get(), getObject()"]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、AutoMeshOperation、MeshOperationManager、Mesh"]
    F1O --> F1S["状态变化：execute()、ExecuteMesh()、RunSimulation()、initializeSolution()、run()"]
    F1S --> F1R["名称/路径/参数：Automated Mesh"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
