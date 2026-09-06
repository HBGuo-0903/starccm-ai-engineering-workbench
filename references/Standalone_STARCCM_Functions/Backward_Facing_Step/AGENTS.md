# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Demo3_Backward_Facing_Step.execute()，并调用 execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), setPresentationName(), byREGEX() 操作 MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot，实现在 STAR-CCM+ simulation 中执行该功能对应的对象配置和结果处理，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：step、Channel、Demo3_Backward_Facing_Step、Block1、x0、x1、y1、y0。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、run()、saveSim()、setPresentationName()、add()、addAll()、generic()。

## Workflow

```mermaid
flowchart TD
    F1["Demo3_Backward_Facing_Step.java / Demo3_Backward_Facing_Step.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → prep1_createParts() → prep2_BCsAndMesh() → prep3_setPost() → run()"]
    F1M --> F1A["实际调用：execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), setPresentationName(), byREGEX(), allByREGEX(), add(), addAll(), getPartGroup()"]
    F1A --> F1O["对象/API 类型：MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot"]
    F1O --> F1S["状态变化：execute()、run()、saveSim()、setPresentationName()、add()、addAll()、generic()"]
    F1S --> F1R["名称/路径/参数：step、Channel、Demo3_Backward_Facing_Step、Block1、x0、x1、y1、y0"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
