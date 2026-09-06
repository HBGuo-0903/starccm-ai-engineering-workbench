// STAR-CCM+ macro: CreateExportTable.java
// Written by STAR-CCM+ 15.02.007
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class CreateExportTable extends StarMacro {

  public void execute() {
    createExportTable();
  }

  private void createExportTable() {

    Simulation simulation_0 = 
      getActiveSimulation();

    XyzInternalTable xyzInternalTable_0 = 
      simulation_0.getTableManager().createTable(XyzInternalTable.class);

    xyzInternalTable_0.setExtractVertexData(true);

    xyzInternalTable_0.getParts().setQuery(null);

    PlaneSection planeSection_0 = 
      ((PlaneSection) simulation_0.getPartManager().getObject("XZ Plane"));

    xyzInternalTable_0.getParts().setObjects(planeSection_0);

    PrimitiveFieldFunction primitiveFieldFunction_0 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Centroid"));

    VectorComponentFieldFunction vectorComponentFieldFunction_0 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(0));

    VectorComponentFieldFunction vectorComponentFieldFunction_1 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(1));

    VectorComponentFieldFunction vectorComponentFieldFunction_2 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_0.getComponentFunction(2));

    PrimitiveFieldFunction primitiveFieldFunction_1 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Velocity"));

    VectorComponentFieldFunction vectorComponentFieldFunction_3 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(0));

    VectorComponentFieldFunction vectorComponentFieldFunction_4 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(1));

    VectorComponentFieldFunction vectorComponentFieldFunction_5 = 
      ((VectorComponentFieldFunction) primitiveFieldFunction_1.getComponentFunction(2));

    PrimitiveFieldFunction primitiveFieldFunction_2 = 
      ((PrimitiveFieldFunction) simulation_0.getFieldFunctionManager().getFunction("Pressure"));

    xyzInternalTable_0.setFieldFunctions(new NeoObjectVector(new Object[] {vectorComponentFieldFunction_0, vectorComponentFieldFunction_1, vectorComponentFieldFunction_2, vectorComponentFieldFunction_3, vectorComponentFieldFunction_4, vectorComponentFieldFunction_5, primitiveFieldFunction_2}));

    xyzInternalTable_0.setPresentationName("Velocity Export XZ");

    TableUpdate tableUpdate_0 = 
      xyzInternalTable_0.getTableUpdate();

    tableUpdate_0.getUpdateModeOption().setSelected(StarUpdateModeOption.Type.TIMESTEP);

    tableUpdate_0.setAutoExtract(true);
    tableUpdate_0.setSaveToFile(true);

    tableUpdate_0.setFilePath("output");
  }
}
