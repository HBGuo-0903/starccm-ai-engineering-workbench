// STAR-CCM+ macro: macro.java
// Written by Danilo Ciliberti for STAR-CCM+ 15.04.010
/*
 Calcola i carichi aerodinamici sull'ala.
 Si assume che l'asse dell'ala sia allineato con l'asse Y (globale),
 altrimenti bisogna prima definire un riferimento locale.

 Read angle of attack from external file.
 Automatically distinguish between inviscid and viscous simulation.
 Works with wings made up of an arbitrary number of panels (just
 define some reference chords at given stations, e.g. root, kink,
 and tip).
 */
package wingLoad;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

import star.common.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;
import star.vis.*;

public class WingLoadBatch extends StarMacro {

	@Override
	public void execute() {
		execute0();
	}

	@SuppressWarnings("deprecation")
	private void execute0() {

		//  INPUT - Grandezze di riferimento
		double b = 24.572;      //  Apertura alare (m)
//		double cr = 2.57;       //  Corda di radice (m)
//		double yk = 4.75;       //  Stazione kink (m)
//		double TR = 0.549;        //  Rapporto di rastremazione
		double[] chords = {2.57, 2.57, 1.41};	// chords distribution (half-wing)
		double[] stations = {0, 4.75, b/2};		// spanwise stations of known chords distribution (right half-wing)
		double V = 49.68;        //  Velocità asintotica (m/s)
		double rho = 1.225;     //  Densità fluido (Kg/m^3)
		// double alpha = 0;       //  Angolo d'attacco (deg)
		double beta = 0;        //  Angolo di derapata (deg)

		//  Distribuzione corde
		double[] PosYCorda = linspace(0.05*b/2, 0.995*b/2, 100);
		//double[] c = cordeATR(cr, b/2, yk, PosYCorda, TR);
		double[] c = corde(chords, stations, PosYCorda);

		//  Nome della regione (tipicamente "Region" o "Region 1")
		String FluidDomain = "Region";

		//  Nome del boundary ala (tipicamente "Fluid.wing" o "Fluid.Wing")
		String Wing = "Wing";
		String Wing_TE = "Wing_TE";
		String Wing_tip = "Wing_tip";
		String Flap = "Flap";
		String Flap_TE = "Flap_TE";
		// add more boundaries names if needed

		//  ESECUZIONE
		Simulation sim
		= getActiveSimulation();

		String alfaFile = resolvePath("aoa");
		double alpha = readAlpha(alfaFile);
//		sim.println("Angle of attack is read from file: " + alfaFile);
//		sim.println("Angle of attack read from file is: " + alpha);


		double q = 0.5 * rho * V * V;     //  Pressione dinamica

		//  Componenti del vettore velocità (e della resistenza Drag)
		double xV = Math.cos(Math.toRadians(alpha)) * Math.cos(Math.toRadians(beta));
		//      double yV = Math.cos(Math.toRadians(alpha)) * Math.sin(Math.toRadians(beta));
		double zV = Math.sin(Math.toRadians(alpha));

		//  Versore normale al vettore velocità (= direzione portanza Lift)   
		double[] DirLift = {-zV, 0, xV};

		// Assegnazione della sezione all'ala
		Region dominio
		= sim.getRegionManager().getRegion(FluidDomain);

		Boundary main1
		= dominio.getBoundaryManager().getBoundary(Wing);
		Boundary main2
		= dominio.getBoundaryManager().getBoundary(Wing_TE);
		Boundary main3
		= dominio.getBoundaryManager().getBoundary(Wing_tip);
		Boundary flap1
		= dominio.getBoundaryManager().getBoundary(Flap);
		Boundary flap2
		= dominio.getBoundaryManager().getBoundary(Flap_TE);
		// add more boundaries if needed


		// Definition of a custom Field Function, the elemental load c(y)dCl(y)
		UserFieldFunction myFieldFun
		= sim.getFieldFunctionManager().createFieldFunction();

		myFieldFun.getTypeOption().setSelected(FieldFunctionTypeOption.SCALAR);
		myFieldFun.setFunctionName("c(y)dCl(y)");
		myFieldFun.setPresentationName("_c(y)dCl(y)");

		// Set reference values for pressure and skin friction coefficients
		PressureCoefficientFunction Cp
		= ((PressureCoefficientFunction) sim.getFieldFunctionManager().getFunction("PressureCoefficient"));
		Cp.getReferenceDensity().setValue(rho);
		Cp.getReferenceVelocity().setValue(V);

		PhysicsContinuum fisica = ((PhysicsContinuum) sim.getContinuumManager().getContinuum("Physics 1"));

		Collection<Model> modelliFisica = fisica.getModelManager().getObjects();

		boolean eulero = modelliFisica.toString().contains("Inviscid");
		if (eulero) {
			sim.println("Inviscid simulation. Only pressure coefficient will be used to calculate aerodynamic loads.");
			
			myFieldFun.setDefinition("${PressureCoefficient}*dot(" + Arrays.toString(DirLift)
			+ ", ($${Area}/mag($${Area})))");		
		} else {
			sim.println("Viscous simulation. Both pressure and skin friction coefficients will be used to calculate aerodynamic loads.");
			
			SkinFrictionCoefficientFunction Cf
			= ((SkinFrictionCoefficientFunction) sim.getFieldFunctionManager().getFunction("SkinFrictionCoefficient"));		
			Cf.getReferenceDensity().setValue(rho);
			Cf.getReferenceVelocity().setValue(V);
			
			myFieldFun.setDefinition("${PressureCoefficient}*dot(" + Arrays.toString(DirLift)
			+ ", ($${Area}/mag($${Area}))) + dot($${WallShearStress}," + Arrays.toString(DirLift)
			+ ") /" + Double.toString(q));
		}


		for (int i = 0; i < Array.getLength(PosYCorda); i++) {

			// Definizione di un piano di sezione
			PlaneSection sezione
			= (PlaneSection) sim.getPartManager().createImplicitPart(new NeoObjectVector(new Object[]{}), new DoubleVector(new double[]{0.0, 1.0, 0.0}), new DoubleVector(new double[]{0, PosYCorda[i], 0}), 0, 1, new DoubleVector(new double[]{0.0}));

			sezione.setPresentationName("Sez." + i);

			sezione.getInputParts().setObjects(main1,main2,main3,flap1,flap2);

			// Definizione dell'integrale di linea (carico di profilo)
			LineIntegralReport Int_dCl
			= sim.getReportManager().createReport(LineIntegralReport.class);

			Int_dCl.setPresentationName("cCl" + i);

			Int_dCl.setScalar(myFieldFun);

			Int_dCl.getParts().setObjects(sezione);

		}

		// Chiama la subroutine per scrivere i carichi in un file .csv
		WriteLoads(PosYCorda, b, c);

	}

	// cordeATR method: distribuzione delle corde lungo l'apertura per un'ala tipo ATR
	private double[] cordeATR(double cr, double b2, double yk, double[] y, double TR) {

		double[] c = new double[Array.getLength(y)];

		for (int i = 0; i < Array.getLength(y); i++) {
			if (y[i] <= yk) {
				c[i] = cr;
			} else {
				c[i] = cr * (1 - (y[i] - yk) / (b2-yk) * (1 - TR));
			}
		}

		return c;
	}
	
	
	// corde method: spanwise chord distribution for a wing made up of n panels
	private double[] corde(double[] chords, double[] stations, double[] y) {
		// This method perform a linear interpolation between the given chords and stations 
		// CHORDS is an array of chords (i.e. root, kink, tip)
		// STATIONS is the array of chords stations (i.e. span station at root, kink, tip)
		// Y are the sections to interpolate spanwise
		
		if (Array.getLength(chords) < 2) {
			System.err.println("Error: the chords array must have at least 2 elements");
		}

		if (Array.getLength(chords) != Array.getLength(stations)) {
			System.err.println("Error: the chords array has different length from the stations array");
			System.out.println("Chords array length: " + Array.getLength(chords));
			System.out.println("Stations array length: " + Array.getLength(stations));
		}

		double[] c = new double[Array.getLength(y)];
		
		int kink = 0;	// first kink station is root chord
		for (int i = 0; i < Array.getLength(y); i++) {
			// if the actual spanwise station is beyond the next kink, increase kink counter (last kink is tip)
			if (y[i] > stations[kink+1]) {
				kink++;
			}
			c[i] = chords[kink] + (chords[kink+1] - chords[kink]) * (y[i] - stations[kink]) / (stations[kink+1] - stations[kink]);
			
			// Debug
			//System.out.println("Sec: " + i +  ", next y kink = " + stations[kink+1] +  ", y(i) = " + y[i] + ", chord(i) = " + c[i]);
		}

		return c;
	}


	// linspace method: distribuzione lineare tra start ed end
	private double[] linspace(double start, double end, int numel) {

		if (numel < 2) {
			System.err.println("Error: the vector must have at least 2 elements");
		}

		if (start > end) {
			System.err.println("Error: the first element is bigger than the last element");
		}

		double[] lineare = new double[numel];
		lineare[0] = start;
		lineare[numel - 1] = end;
		double inc = (end - start) / (numel - 1);

		for (int i = 1; i < numel - 1; i++) {
			lineare[i] = lineare[i - 1] + inc;
		}

		return lineare;
	}

	// cosine method: distribuzione delle corde con legge del coseno
	// l'elemento end deve coincidere con la semiapertura alare b/2 affinché
	// tutto abbia senso.
//	private double[] cosine(double start, double end, int numel) {
//
//		if (numel < 2) {
//			System.err.println("Error: the vector must have at least 2 elements");
//		}
//
//		if (start > end) {
//			System.err.println("Error: the first element is bigger than the last element");
//		}
//
//		double[] cy = new double[numel];
//		cy[0] = end;
//		cy[numel - 1] = start;
//
//		double[] theta = linspace(0, Math.PI / 2, numel);
//
//		for (int i = numel - 2; i > 0; i--) {
//			cy[i] = end * Math.cos(theta[i]);
//		}
//
//		return cy;
//	}

	// WriteLoads method: legge i report cCl e scrive un file .csv con i carichi
	private void WriteLoads(double[] y, double b, double[] c) {

		BufferedWriter bwout = null;

		try {

			Simulation sim = getActiveSimulation();

			// Collecting the simulation file name
			String simulationName = sim.getPresentationName();
			//            sim.println("Simulation Name:" + simulationName);

			// Open Buffered Input and Output Readers
			// Creating file with name "<sim_file_name>+report.csv"
			bwout = new BufferedWriter(new FileWriter(resolvePath(simulationName + "_loads.csv")));
			bwout.write("y, eta, c, cCl, Cl \n");

			for (int i = 0; i < Array.getLength(y); i++) {
				Report RepName = sim.getReportManager().getReport("cCl" + Integer.toString(i));
				Double cCl = RepName.getReportMonitorValue();
				Double Cl = cCl / c[i];

				bwout.write(y[i] + ", " + y[i] / (b/2) + ", " + c[i] + ", " + cCl + ", " + Cl + "\n");
			}
			bwout.close();

		} catch (IOException iOException) {
		}
	}



	// Read angle of attack from external file
	private double readAlpha(String alfaFile){

		double alpha = 999; // initialization
		try {
			File myObj = new File(alfaFile);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				alpha = Double.parseDouble(data);
				System.out.println("Angle of attack read from file is: " + alpha);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return alpha;
	}
}