# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Normals2.execute()，并调用 execute(), execute0(), getActiveSimulation(), getPartManager(), getObject(), getRepresentationManager(), exportDataSourceSTL(), resolvePath() 操作 StarMacro、Simulation、FvRepresentation、Mesh、Scene、CurrentView，实现配置场景、视图和可视化对象以生成可检查的结果，解决场景显示和可视化输出依赖重复手工设置。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Y Normal、Volume Mesh、N:\\1 - USM23 CAD\\Cooling\\CFD\\Radiator\\Old Sims\\YNormal.stl、Turbulent Kinetic Energy。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、FvRepresentation、Mesh、Scene、CurrentView。
- 代码执行的结果操作：execute()、execute0()、exportDataSourceSTL()、setInput()。

## Workflow

```mermaid
flowchart TD
    F1["Normals2.java / Normals2.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → execute0()"]
    F1M --> F1A["实际调用：execute(), execute0(), getActiveSimulation(), getPartManager(), getObject(), getRepresentationManager(), exportDataSourceSTL(), resolvePath(), getSceneManager(), getScene()..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、FvRepresentation、Mesh、Scene、CurrentView"]
    F1O --> F1S["状态变化：execute()、execute0()、exportDataSourceSTL()、setInput()"]
    F1S --> F1R["名称/路径/参数：Y Normal、Volume Mesh、N:\\1 - USM23 CAD\\Cooling\\CFD\\Radiator\\Old Sims\\YNormal.stl、Turbulent Kinetic Energy"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
