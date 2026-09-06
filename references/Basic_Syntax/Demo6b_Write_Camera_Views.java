// 基础 API 示例：将当前 simulation 的场景相机视图写入文本文件。
// 前置条件：在 STAR-CCM+ 中打开目标 simulation，并确保 MacroUtils 可用。
import macroutils.MacroUtils;
import star.common.StarMacro;

public class Demo6b_Write_Camera_Views extends StarMacro {

    @Override
    public void execute() {

        new MacroUtils(getActiveSimulation()).io.write.cameraViews("myCameras.txt");

    }

}
