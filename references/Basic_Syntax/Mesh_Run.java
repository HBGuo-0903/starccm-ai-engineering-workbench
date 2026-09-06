// 基础 API 示例：更新当前 simulation 的体网格并执行一次运行操作。
// 该文件只用于展示 MacroUtils 的 volumeMesh() 和 run() 调用，不是完整案例。

import star.common.*;
import macroutils.*;

public class Mesh_Run extends StarMacro {

    public void execute() {
        mu = new MacroUtils(getSimulation());
        ud = mu.userDeclarations;
        mu.update.volumeMesh();
        mu.run();
    }
    
    MacroUtils mu;
    UserDeclarations ud;
}
