# STAR-CCM+ API 简单功能语法

本文件收录从 `Standalone_STARCCM_Functions` 中识别出的简单 Java 宏功能。对应的 Java 示例直接放在 `Basic_Syntax` 根目录中，不再创建案例目录、`README.md` 或 `AGENTS.md`；Java 文件内的注释负责说明用途、前置条件和关键 API。

## 如何判断是否属于这里

如果代码满足以下特征，优先放在基础语法中：

- 只有一个很短的 `execute()` 方法；
- 只获取一个 STAR-CCM+ 对象并调用一个设置方法；
- 没有独立的输入文件、网格流程、求解流程或结果验证；
- 没有多个执行步骤，也没有值得单独复用的工程工作流；
- 主要学习价值是展示某个 API 类、对象管理器或属性设置方法。

如果一个宏虽然代码不长，但需要特定模型树、多个对象、外部数据或明确的工程前置条件，则仍应保留为独立功能目录。

## 宏入口

STAR-CCM+ 宏通常继承 `StarMacro`，入口是 `execute()`：

```java
import star.common.*;

public class ExampleMacro extends StarMacro {
    @Override
    public void execute() {
        Simulation simulation = getActiveSimulation();
        // 在这里访问或修改 simulation 中的对象
    }
}
```

## 获取当前 Simulation

```java
Simulation simulation = getActiveSimulation();
```

`getActiveSimulation()` 适用于在 STAR-CCM+ 当前打开的 simulation 上运行宏。不要把这个简单调用单独维护成案例。

## 连续体和物理模型

简单的连续体创建、获取和模型切换属于 API 基础语法：

```java
Continuum continuum = simulation.getContinuumManager()
        .getContinuum("Physics 1");

continuum.getModelManager().getModel(LaminarModel.class);
```

常见操作包括：

- 创建或获取 Continuum；
- 选择 Laminar、LES、RANS 或 RST 模型；
- 启用等温模型；
- 获取稳态物理连续体；
- 切换冻结流或隐式非稳态设置。

这些操作只有在和网格、边界、求解及结果处理组成完整流程时，才需要提升为独立案例。

## Field Function

创建、命名和修改 Field Function 的最小语法：

```java
UserFieldFunction function = simulation.getFieldFunctionManager()
        .createFieldFunction();
function.setFunctionName("MyFunction");
function.setDefinition("${VelocityMagnitude}");
```

Tensor Field Function、用户变量和 Field Function 重命名也属于同一类基础 API 用法。

## Solver 和时间步

设置时间步、二阶时间离散、内迭代次数和停止条件的核心形式是获取对象后调用设置方法：

```java
Solver solver = continuum.getSolverManager()
        .getSolver(SegregatedFlowSolver.class);
solver.getTimeStep().setValue(0.001);
```

常见基础设置包括：

- 设置时间步；
- 设置自适应时间步；
- 设置二阶时间步；
- 设置内迭代次数；
- 设置最大物理时间或停止条件；
- 初始化解；
- 启动一次迭代或运行。

## 自动保存和简单保存

自动保存、按迭代频率保存和保存当前 simulation 属于保存 API 的基础用法：

```java
SimulationIterator iterator = simulation.getSimulationIterator();
AutoSave autoSave = iterator.getAutoSave();
autoSave.setEnabled(true);
autoSave.setMaxAutosavedFiles(5);
```

如果保存操作只是某个更大案例中的一步，应写在该案例的工作流说明中，不再单独建立案例目录。

## 场景、视图和显示属性

修改当前视图、隐藏场景轮廓、切换显示 Field Function 等短操作可作为基础语法：

```java
Scene scene = simulation.getSceneManager().getScene("Scalar Scene");
ScalarDisplayer displayer =
        (ScalarDisplayer) scene.getDisplayerManager().getObject("Scalar 1");
displayer.getScalarDisplayQuantity().setFieldFunction(fieldFunction);
```

真正创建多个场景、设置多个显示器、导出结果或批量处理 simulation 的代码，仍保留为独立功能。

## 边界和对象访问

按名称获取 Region、Boundary、Part 或 Plane 并进行一次分配，通常只是对象访问语法：

```java
Region region = simulation.getRegionManager().getRegion("Region 1");
Boundary boundary = region.getBoundaryManager().getBoundary("Inlet");
```

当代码同时完成多个区域、边界、网格或物理设置时，应按照完整案例处理。

## 已归档的简单宏

以下原目录已从 `Standalone_STARCCM_Functions` 中移出，不再作为独立案例维护。可直接使用的 Java 示例位于本目录根目录；原始目录和配套说明保留在 `98_Previous_Organization\Simplified_Basic_Functions`：

- Continuum 和物理模型：`Create_Physics`、`Add_Steady_Physics_Continuum`、`Add_Isothermal_Model`、`Laminar`、`LES`、`RST`、`Set_RANS`；
- Solver 和运行控制：`Freeze_Flow`、`Set_LES_Cw`、`Set_Adaptive_Time_Step`、`Set_Auto_Save`、`Set_Auto_Save_Iteration`、`Set_Inner_Iterations`、`Set_Second_Order_Timestep`、`Set_Stopping_Criteria`、`Set_Time_Step`、`Set_Stop_Run`、`Switch_To_Implicit_Unsteady`、`Solution_Initialization`、`Start_Iterating`、`Mesh_Run`、`Run_And_Close_Simulation`、`Continue`、`Simple_Save`；
- Field Function：`Create_Field_Functions`、`Create_Field_Functions_And_Expressions`、`Create_Tensor_Field_Functions`、`Rename_Field_Function`；
- 场景和视图：`Change_Perspective`、`Paralell_View`、`Hide_Outline`、`Set_Field_Function_Variable`、`Scalar_Change`；
- 网格、派生部件和对象操作：`Create_Derived_Parts`、`Apply_Derived_Part_To_Scene`、`Create_Line_Probe`、`Execute_Mesh`、`Mesh`、`Meshing`、`Part_Mesh`、`Region_Mesh`、`Prism_Layer_Thickness`、`Set_Prim_Layers`、`Set_Custom_Surface_Size_On_Refinement_Zone`、`Simple_Hexa_Mesher`、`Wrap_Mesh`；
- 边界、后处理和输出：`Add_Parts_To_Scalar`、`Convection_Only_Scalar`、`Set_Outlet_Backflow_Concentration`、`Set_Rotation_Of_Rotor`、`Set_Velocity_Mag_Inlet`、`Record_History`、`Record_Mixing`、`Export_Shear`、`Export_Shear_RST`、`Export_Torque`、`Function_Names`、`Configure_Views_And_Perspectives`、`Write_Camera_Views`、`Make_Me_Pretty`；
- 简短工作流：`Add_Rev_And_Run`、`Change_To_Tracing`、`Set_Cubic_Linear`；
- 空壳或单一入口：`Enable_User_Lib`、`Add_User_Lib`、`Template`。

这些宏的代码仍可在归档目录中追溯；活动资料中只保留可复用的语法说明，不保留没有独立案例价值的目录外壳。
