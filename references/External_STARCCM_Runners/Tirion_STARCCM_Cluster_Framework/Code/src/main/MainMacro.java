// STAR-CCM+ macro: MainMacro.java
// Last Modified by Viktor Velev (Formula Student Team Delft)
package macro;

import java.util.*;
import java.net.*;
import java.io.*;
import star.common.*;
import star.base.neo.*;
 
import star.common.*;
import star.coupledflow.*;
import star.base.neo.*;
import star.flow.*;
import star.meshing.*;
import java.nio.file.Paths;

/** 
 * Brace yourself. You will see some dark magic 
 * JVM tricks to be able to modulize
 * the whole framework while working with STAR-CCM 
 * [JVMH] - An annotation for JVM Hack comments explaining stuff
 * */

public class MainMacro extends StarMacro {
	
	/** 
	 * This script assumes it's running environment 
	 * is the STAR-CCM+ 11.04 Java Virtual Machine (JVM) 
	 * 		- Has STAR-CCM Server configured 
	 * 		and running with the loaded .sim file
	 * */

	/** Change 'maximumIterations' to X to run */
	
	int maximumIterations = 3500;

	/** [JVMH] For future Inter cross-JVM communication hacks */
	// Class CFDPipeline;
	// pulic String pwd = Paths.get(".").toAbsolutePath()
	// public String projectPath = "/home/viktorv/Projects/FSTeamDelft/Tirion/";
	
	public Simulation simulation;

	private MeshPipelineController meshPipelineController;
	private SolverStoppingCriterionManager solverCriterionManager;

	private String OS = System.getProperty("os.name").toLowerCase();

	public void execute() {

		/** The whole sequential CFD pipeline */
		// [JVMH] manageImports();
		initialize();
		runSimulation(maximumIterations);
		// runPostProcessing();
		// csvDataExport();
		// pythonPostProcExport();

		/** 
		 * You can just comment out any step of the pipeline and the rest will work 
		 * (keep initialization though) 
		 * */
	}

	private void initialize() {
		/** Get simulation environment, initialize global variables, initalize database structure */
		
		simulation = getActiveSimulation();
		initializeDatabase();
	}

	private void runSimulation(int maxIterations) {

		log("Meshing...");

		/** 
		 * Get the needed criterion, modify them and run the simulation 
		 * The simulation will stop if the number of iterations has reached 
		 * the previously specified "maximumIterations" (class global)
		 * */

		meshPipelineController = simulation.get(MeshPipelineController.class);
		solverCriterionManager = simulation.getSolverStoppingCriterionManager();
		
		meshPipelineController.generateVolumeMesh();
		StepStoppingCriterion stepStoppingCriterion = (StepStoppingCriterion) solverCriterionManager
			.getSolverStoppingCriterion("Maximum Steps");
		
		stepStoppingCriterion.setMaximumNumberSteps(maxIterations);
	
		simulation.getSimulationIterator().run();
	}

	public void runPostProcessing() {
		/** 
		 * TODO: Import from another PostProcessing class the needed methods 
		 * and use them here on the already initialized STAR-CCM Environment 
		 * and after running the specified number of iterations 
		 * 		-- Need to get the Inter-cross JVM communication working in
		 * 		   order for the ultimate software architecture to occur.
		 * */

		log("Running post processing... [!stub]");

		/** Code for post processing goes here
		 * TODO: Implement and optimize properly the post processing code
		 * use .export for the png exporting
		 */

	}

	public void initializeDatabase() {
		/** 
		 * This should act as an interface between the actual processing and 
		 * saving files (in a Database or just FilesystemDB)
		 * 
		 * TODO: Implement this after implementing the post processing 
		 * TODO: Implement post-post-processing, concating pngs into videos with ffmpeg 
		 * 
		 * Initializing consists of creating the folders/db models/
		 * Connecting to the database.
		 * 
		 * TODO: Things to consider: use Ignite || use Postgres + Redis || use MongoDB
		 * 
		 * */
	}

	public void checkpoint(String path) {
		simulation.saveState(path);
	}

	public void export() {
		/** 
		 * This should act as an interface between the actual processing and 
		 * saving files (in a Database or just FilesystemDB)
		 * 
		 * TODO: Implement this after implementing the post processing 
		 * TODO: Implement post-post-processing, concating pngs into videos with ffmpeg 
		 * 
		 * */

		log("STUB!");
	}

	private void csvDataExport() {

		/**
		 * Full export data (residuals) to .csv
		 * 
		 * Previously defined process in the full_export.java #Deprecated#
		 */

		String pathName, simName, mainFolderName, simPath, figName, saveName;

		simName = simulation.getPresentationName();
		simPath = simulation.getSessionDir();
		
		String separator = ((OS.indexOf("win") >= 0) ? "\\" : "/");
		mainFolderName = simPath + separator + "PlotsCSV#" + simName;
		
		log("Saving output to: " + mainFolderName);
		new File(mainFolderName).mkdir();
		
		int all = simulation.getPlotManager().getObjects().size();
		int i = 0;

		for (StarPlot plot : simulation.getPlotManager().getObjects()) {
			
			figName = plot.getPresentationName();
			MonitorPlot monitorPlot = ((MonitorPlot) simulation.getPlotManager().getPlot(figName));
			monitorPlot.export(resolvePath(mainFolderName + separator + figName + ".csv"), ",");	
			// log(resolvePath(mainFolderName + separator + figName + ".csv"));
			i++;
			progress(i, all);
		}
	}

	private void pythonPostProcExport() {
		/**
		 * Basic data perparation with pandas and numpy
		 * 
		 * STUB
		 */
		log("[pythonPostProcExport()]: !stub");
	}

	/** For JVM Hacks */

	private void manageImports() {
		/** [JVMH] For future Inter cross-JVM communication hacks */

		log(Paths.get(".").toAbsolutePath().normalize().toString());
		//log("file://" + projectPath + "/scripts/CFDPipeline.java");
		log("!stub");

		// [JVMH]
		// URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
		// 	new URL("file://" + projectPath + "/scripts/CFDPipeline.java")
		// });
		
		// CFDPipeline = urlClassLoader.loadClass("deep");
	}

	/** End of JVM Hacks */

	/** Utility Functions */

	private void checkpointLog(String what, String where) {
		log(String.format("Saved %s at %s.", what, where));
	}

	private void progress(int done, int all) {
		StringBuilder progressBar = new StringBuilder();

		for(int i = 0; i < all; i++) {
			if (i < done) {
				progressBar.append("#");
			} else {
				progressBar.append("-");
			} 
		}

		log(
			String.format(
				"Progress: [%s] %d/%d -> %.2f%%", 
				progressBar.toString(), done, all, 
				(((float) done/ (float) all))*100
			)
		);
	}

	private void log(Object x) {
		/** A logging utility for debugging and additional information. Needs initialization beforehand */
		// System.out.println("[LOG]: " + x); // This logs in the terminal if you've run STAR-CCM through shell.
		simulation.println("[LOG]: " + x); // This logs in the output window in the program itself.
	}
}
