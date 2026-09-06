# Case Macro Directory

## Summary

本宏通过在 STAR-CCM+ 中执行 flow_simulator.execute()，并调用 execute(), execute0(), getActiveSimulation(), getSceneManager(), createScene(), initializeAndWait(), getUnitsManager(), getPreferredUnits() 操作 StarMacro、Simulation、Scene、Units、FileTable、SolidModelManager、CadModel、SceneUpdate，实现在 STAR-CCM+ simulation 中执行该功能对应的对象配置和结果处理，解决该类 STAR-CCM+ 模型处理依赖重复手工操作。

## Input

- Input_Files\pore_bodies_1.csv：代码或案例运行所需的输入文件。
- Input_Files\pore_throats_1.csv：代码或案例运行所需的输入文件。
- Input_Files\Star.sim：代码或案例运行所需的输入文件。
- 代码中引用的对象名称或参数：3D-CAD View、Loading files.、./Data/pore_bodies_、.csv、./Data/pore_throats_、simulation_、Existing simulation.、New simulation.。运行前需确认模型树中名称一致。

## Output

- 被创建、读取或修改的 STAR-CCM+ 对象：StarMacro、Simulation、Scene、Units、FileTable、SolidModelManager、CadModel、SceneUpdate。
- 代码执行的结果操作：execute()、execute0()、createScene()、initializeAndWait()、createFromFile()、add()。

## Workflow

```mermaid
flowchart TD
    F1["flow_simulator.java / flow_simulator.execute(): 文件入口"]
    F1 --> F1M["方法链：execute() → execute0()"]
    F1M --> F1A["实际调用：execute(), execute0(), getActiveSimulation(), getSceneManager(), createScene(), initializeAndWait(), getUnitsManager(), getPreferredUnits(), getCoordinateSystemManager(), g..."]
    F1A --> F1O["对象/API 类型：StarMacro、Simulation、Scene、Units、FileTable、SolidModelManager、CadModel、SceneUpdate、HardcopyProperties、CurrentView、Body、Face"]
    F1O --> F1S["状态变化：execute()、execute0()、createScene()、initializeAndWait()、createFromFile()、add()"]
    F1S --> F1C1773185267{"控制流：try {"}
    F1C1773185267 --> F1C484221577{"控制流：if(checker.exists()){"}
    F1C484221577 --> F1C112210981{"控制流：if (!Double.isNaN(pore_bodies_array[i][0])){"}
    F1C112210981 --> F1C466912619{"控制流：if (!Double.isNaN(pore_throats_array[i][0])){"}
    F1C466912619 --> F1R["名称/路径/参数：3D-CAD View、Loading files.、./Data/pore_bodies_、.csv、./Data/pore_throats_、simulation_、Existing simulation.、New simulation."]
    F1R --> OUT["验证：检查上述对象、simulation 状态和代码产生的文件"]
```

## Maintenance

- `Code` 只保留属于本功能边界的 Java 文件；如果新增文件不能接入当前执行链，应建立新的功能目录。
- 修改对象名称、输入路径、参数或执行顺序后，必须同步更新本文件的 `Input`、`Output` 和 Mermaid `Workflow`。
