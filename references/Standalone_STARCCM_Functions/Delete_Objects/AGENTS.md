# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 deleteObjects.execute()，并调用 execute(), deleteObjects(), getActiveSimulation(), getSceneManager(), getObjects(), getPresentationName(), deleteScene(), deleteScenes() 操作 StarMacro、Simulation、Scene、Continuum、MeshOperationManager、MeshOperation，实现在 STAR-CCM+ simulation 中执行该功能对应的对象配置和结果处理，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Geometry Scene 1、Deleted scene 、All scenes sucessfully deleted、Physics 1、Deleted continuum 、All physics continua sucessfully detelted.、Deleted operation、All operations sucessfully deleted。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Scene、Continuum、MeshOperationManager、MeshOperation。
- 代码执行的结果操作：execute()、deleteObjects()、deleteScene()、deleteScenes()、removeObjects()。

## Workflow

```mermaid
flowchart TD
    F1["deleteObjects.java / deleteObjects.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → deleteObjects()"]
    F1M --> F1A["实际调用：execute(), deleteObjects(), getActiveSimulation(), getSceneManager(), getObjects(), getPresentationName(), deleteScene(), deleteScenes(), getObjectsOf(), getContinuumManage..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Scene、Continuum、MeshOperationManager、MeshOperation"]
    F1O --> F1S["状态变化：execute()、deleteObjects()、deleteScene()、deleteScenes()、removeObjects()"]
    F1S --> F1C530642594{"控制流：if (!sim.getSceneManager().isEmpty()) {"}
    F1C530642594 --> F1C273456963{"控制流：for (Scene s : Scenes) {"}
    F1C273456963 --> F1C1820521955{"控制流：if (!s.getPresentationName().equals('Geometry Scene 1')) {"}
    F1C1820521955 --> F1C590723621{"控制流：if (!sim.getContinuumManager().isEmpty()) {"}
    F1C590723621 --> F1C1282042833{"控制流：if (!sim.getRegionManager().isEmpty()) {"}
    F1C1282042833 --> F1R["名称/路径/参数：Geometry Scene 1、Deleted scene 、All scenes sucessfully deleted、Physics 1、Deleted continuum 、All physics continua sucessfully detelted.、Deleted operation、All operations ..."]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
