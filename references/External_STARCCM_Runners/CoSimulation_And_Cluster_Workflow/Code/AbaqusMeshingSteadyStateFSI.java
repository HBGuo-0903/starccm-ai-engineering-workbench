package myStarJavaMacros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import star.common.ImplicitUnsteadyModel;
import star.common.PhysicsContinuum;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.SteadyModel;
import starClasses.ContiuumBuilder;
import starClasses.ImportCAE;
import starClasses.MeshMorpher;
import starClasses.SolversNode;
import starClasses.StoppingCriteria;

public class AbaqusMeshingSteadyStateFSI extends StarMacro 
{
	double[] m_timeStep;
	public void execute() 
	{
		String currentDirectory = System.getProperty("user.dir");
		this.readTimeStepFile(currentDirectory + File.separator + "TimeSteps.txt");
		
		for (int i = 0; i < m_timeStep.length; i++)
		{
			// Star-CCM+ settings and variables
			Simulation activeSim = getActiveSimulation();
			double timeStep = m_timeStep[i];
			/**-----------------------------------------------------------------------------------------------------------------------------------------------------
				IMPORTED PARTS NODE */
			// Importing Abaqus plate part and its initial deflection solution
			String fileLocation = currentDirectory + File.separator;
			String abqFileName = "DeflectedPlate";
			String[] regionDeflectionMapping = {"Fluid", "Fluid", "Fluid", "Fluid"};
			String[] boundaryDeflectionMapping = {"Fluid.FSI_Front", "Fluid.FSI_Back", "Fluid.FSI_Top", "Fluid.FSI_Bottom"};
			ImportCAE cae = new ImportCAE(activeSim, fileLocation, "Fluid");
			cae.importAbaqusInputFile(abqFileName, false, true);
			cae.importAbaqusOdbFile(abqFileName, "SS_Deflection");
			cae.deformImportedAbaqusModel();
			cae.mapAbaqusDeflectionData(regionDeflectionMapping, boundaryDeflectionMapping, "Plate.FSI_INTERFACE");
				
			/**-----------------------------------------------------------------------------------------------------------------------------------------------------
				SOLVERS NODE */
			SolversNode solvers = new SolversNode(activeSim);
			solvers.setKepsilonRelax(0.6);
			solvers.setUnsteadyTimeStep(timeStep, 1);
			
			// Setting up the mesh morpher solver
			MeshMorpher morpher =  new MeshMorpher(activeSim, "Fluid");
			morpher.addRegionBoundary("Fluid.FSI_Front", "SS");
			morpher.addRegionBoundary("Fluid.FSI_Back", "SS");
			morpher.addRegionBoundary("Fluid.FSI_Top", "SS");
			morpher.addRegionBoundary("Fluid.FSI_Bottom", "SS");
			
			/**-----------------------------------------------------------------------------------------------------------------------------------------------------
				RUNNING THE SIMULATION */
			activeSim.getSimulationIterator().run(500);
			activeSim.saveState(currentDirectory + File.separator + "SS_Deflection_TS_" + timeStep*10000);
		}
	}
	
	private void readTimeStepFile(String file2Read)
	{
		FileReader file;
		try 
		{
			file = new FileReader(file2Read);
			BufferedReader reader = new BufferedReader(file);
			
			String line;
			int i = 0;
			while((line=reader.readLine()) != null)
			{
				if(line.startsWith("*"))
				{
					continue;
				}
				else
				{
				
					 m_timeStep[i] = Double.parseDouble(line);
				}
				i = i++;
			}
			reader.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			JOptionPane.showMessageDialog(null, e.toString());
		} 
		catch (NumberFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
