# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Demo2_Conduction_In_a_Channel.execute()，并调用 execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), setPresentationName() 操作 MacroUtils、UserDeclarations、StarMacro、Scene、Mesh、Material、Solver、Plot，实现在 STAR-CCM+ simulation 中执行该功能对应的对象配置和结果处理，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo2_Conduction_In_a_Channel、myCam|5.003809e-01,1.413476e-02,4.204865e-03、|-1.256561e+00,7.844162e-01,1.422375e+00|2.630476e-01,9.462717e-01,-1.880847e-01、|2.280761e-01|1、Channel、.*、x0、x1。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro、Scene、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()、addAll()。

## Workflow

```mermaid
flowchart TD
    F1["Demo2_Conduction_In_a_Channel.java / Demo2_Conduction_In_a_Channel.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → prep1_createPart() → prep2_createRegion() → prep3_BCsAndMesh() → prep4_setPost() → run()"]
    F1M --> F1A["实际调用：execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), setPresentationName(), add(), byREGEX(), allByREGEX(), generic()"]
    F1A --> F1O["对象/API 类型：MacroUtils、UserDeclarations、StarMacro、Scene、Mesh、Material、Solver、Plot"]
    F1O --> F1S["状态变化：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()、addAll()"]
    F1S --> F1R["名称/路径/参数：Demo2_Conduction_In_a_Channel、myCam|5.003809e-01,1.413476e-02,4.204865e-03、|-1.256561e+00,7.844162e-01,1.422375e+00|2.630476e-01,9.462717e-01,-1.880847e-01、|2.280761e-0..."]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
