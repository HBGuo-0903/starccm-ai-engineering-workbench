# Basic Syntax Reference

## Organization Rules

1. 本目录只存放基础语法、宏结构、API 使用模式和基础开发方法等文档。
2. 每个 Markdown 文件对应一个独立的知识点或主题，文档直接放在 `Basic_Syntax` 根目录下。
3. 对于只有一个或几个基础 API 调用、没有独立案例价值的宏，Java 文件直接放在本目录根目录中，不创建案例子目录。
4. 基础 Java 示例不创建单独的 `README.md` 或 `AGENTS.md`；用途、前置条件和关键 API 通过 Java 文件内的注释说明。
5. 不在本目录放置输入文件、仿真案例、外部运行项目或完整工具项目；原始宏如需追溯，统一放在 `98_Previous_Organization`。

## Document Rules

1. 文档标题应直接使用主题名称，不用序号

## Current Layout

当前目录采用“根目录 + Markdown 文档或源代码”的扁平结构：

```text
Basic_Syntax/
├─ AGENTS.md
├─ CreateFieldFunctions.java
├─ SetTimeStep.java
├─ Boundary_Conditions.md
├─ Macro_Structure.md
├─ Mesh_Generation.md
└─ ...
```

简单 Java 示例与 Markdown 知识点文档可以并列存在，但不能为单个简单示例增加目录外壳。任何后续整理都必须保持上述结构和分类边界。
