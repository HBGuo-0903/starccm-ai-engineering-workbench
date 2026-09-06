# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Demo1_Flow_In_a_Pipe.execute()，并调用 execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), byREGEX() 操作 MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Demo1_Flow_In_a_Pipe、myCam|2.498938e-01,-1.450833e-02,-2.222717e-02、|2.498938e-01,-1.450833e-02,9.690091e-01|0.000000e+00,1.000000e+00,0.000000e+00、|1.463440e-01|1、x0、x1、Def.*、My Mesh。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot。
- 代码执行的结果操作：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()。

## Workflow

```mermaid
flowchart TD
    F1["Demo1_Flow_In_a_Pipe.java / Demo1_Flow_In_a_Pipe.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → initMacro() → prep1_createPart() → prep2_createRegion() → prep3_BCsAndMesh() → prep4_setPost() → run()"]
    F1M --> F1A["实际调用：execute(), initMacro(), run(), saveSim(), all(), getActiveSimulation(), cameraView(), byREGEX(), setPresentationName(), add(), getPresentationName(), generic()"]
    F1A --> F1O["对象/API 类型：MacroUtils、UserDeclarations、StarMacro、Mesh、Material、Solver、Plot"]
    F1O --> F1S["状态变化：execute()、run()、saveSim()、setPresentationName()、add()、generic()、stoppingCriteria()"]
    F1S --> F1R["名称/路径/参数：Demo1_Flow_In_a_Pipe、myCam|2.498938e-01,-1.450833e-02,-2.222717e-02、|2.498938e-01,-1.450833e-02,9.690091e-01|0.000000e+00,1.000000e+00,0.000000e+00、|1.463440e-01|1、x0、x..."]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
