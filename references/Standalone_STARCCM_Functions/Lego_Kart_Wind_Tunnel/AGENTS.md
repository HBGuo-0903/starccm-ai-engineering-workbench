# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 Demo5_Lego_Kart_Wind_Tunnel.execute()，并调用 execute(), initMacro(), run(), all(), saveSim(), getCarBoundaries(), allByREGEX(), getInlet() 操作 MacroUtils、UserDeclarations、Boundary、Simulation、StarMacro、AutoMeshOperation、Region、Mesh，实现按案例参数组织几何、物理、网格、求解和后处理步骤，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- 已打开的 STAR-CCM+ simulation，以及代码中通过名称获取的已有对象。
- 代码中引用的对象名称或参数：Tunnel、Kart Wrap、^((?!、).)*$、.*、Demo5_Lego_Kart_Wind_Tunnel、Geometry already created. Skipping prep1...、cam1|-2.491101e-03,1.572050e-02,-9.156700e-03。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：MacroUtils、UserDeclarations、Boundary、Simulation、StarMacro、AutoMeshOperation、Region、Mesh。
- 代码执行的结果操作：execute()、run()、saveSim()、importPart()、open()、addAll()、setPresentationName()。

## Workflow

```mermaid
flowchart TD
    F1["Demo5_Lego_Kart_Wind_Tunnel.java / Demo5_Lego_Kart_Wind_Tunnel.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → getCarBoundaries() → getInlet() → getOutlet() → initMacro() → prep1_importGeometry() → prep2_createTunnelWrapAndRegion() → prep3_createBCsAndMesh() → prep4_setPo..."]
    F1M --> F1A["实际调用：execute(), initMacro(), run(), all(), saveSim(), getCarBoundaries(), allByREGEX(), getInlet(), byREGEX(), getOutlet(), getActiveSimulation(), getGeometryPartManager()"]
    F1A --> F1O["对象/API 类型：MacroUtils、UserDeclarations、Boundary、Simulation、StarMacro、AutoMeshOperation、Region、Mesh、Material、Solver、Scene、Plane"]
    F1O --> F1S["状态变化：execute()、run()、saveSim()、importPart()、open()、addAll()、setPresentationName()"]
    F1S --> F1C1272385628{"控制流：if (!sim.getGeometryPartManager().isEmpty()) {"}
    F1C1272385628 --> F1C1282042833{"控制流：if (!sim.getRegionManager().isEmpty()) {"}
    F1C1282042833 --> F1C2107503585{"控制流：if (mu.check.has.volumeMesh()) {"}
    F1C2107503585 --> F1C1633037737{"控制流：if (!sim.getReportManager().isEmpty()) {"}
    F1C1633037737 --> F1R["名称/路径/参数：Tunnel、Kart Wrap、^((?!、).)*$、.*、Demo5_Lego_Kart_Wind_Tunnel、Geometry already created. Skipping prep1...、cam1|-2.491101e-03,1.572050e-02,-9.156700e-03"]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
