# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 AddPassiveScalars.execute()，并调用 execute(), AddPassiveScalar(), getActiveSimulation(), getContinuumManager(), getContinuum(), getModelManager(), getModel(), getPassiveScalarManager() 操作 StarMacro、Simulation、PhysicsContinuum，实现在 simulation 对象树中创建、配置或分配目标对象，解决模型对象需要人工重复创建、配置或分配。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：yBa_2+、ySO4_2-、yNa_1+、yCl_1-、Physics 1。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、PhysicsContinuum。
- 代码执行的结果操作：execute()、AddPassiveScalar()、createPassiveScalarMaterial()、setSelected()、setMaxAllowable()、setPresentationName()。

## Workflow

```mermaid
flowchart TD
    F1["AddPassiveScalars.java / AddPassiveScalars.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → AddPassiveScalar()"]
    F1M --> F1A["实际调用：execute(), AddPassiveScalar(), getActiveSimulation(), getContinuumManager(), getContinuum(), getModelManager(), getModel(), getPassiveScalarManager(), createPassiveScalarMa..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、PhysicsContinuum"]
    F1O --> F1S["状态变化：execute()、AddPassiveScalar()、createPassiveScalarMaterial()、setSelected()、setMaxAllowable()、setPresentationName()"]
    F1S --> F1C961411448{"控制流：for(String scalar : scalars) {"}
    F1C961411448 --> F1R["名称/路径/参数：yBa_2+、ySO4_2-、yNa_1+、yCl_1-、Physics 1"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
