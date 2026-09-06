/* readFile.java
   read text file that contains mesh setting info, then print it to the CCM console
   Use Scanner in java.util
   Please check meshSettings.txt for the format of an input file
 */



import java.io.File;
import java.util.Locale;
import java.util.Scanner;

import star.common.StarMacro;


public class readFile extends StarMacro {

    
    public void execute() {
	readTextFiles();    
    }
    public void readTextFiles(){
	String fileName = "meshSettings.txt"; // File that contains Mesh information
        printFileInfo(resolvePath(fileName));
		
        double baseSize = readFileDouble(resolvePath(fileName), "Base Size");
	double numPrismLayers = readFileDouble(resolvePath(fileName), "Number of Prism Layers");
	double sizePrismLayer = readFileDouble(resolvePath(fileName), "Prism Layer Absolute Size");
    }

    public double readFileDouble(String file, String pattern) {
        try (Scanner sc = new Scanner(new File(file)).useLocale(Locale.ENGLISH)) {
            String find = sc.findWithinHorizon(pattern, 0);
            if (find != null) {
                double result = sc.nextDouble();
                getSimulation().println(pattern + "found. Set the corresponding variable to " + result);
                return result;
            }
        } catch (Exception e) {
            getSimulation().println(e);
        }
        return 0;
    }

    public void printFileInfo(String file) {
        try (Scanner sc = new Scanner(new File(file)).useLocale(Locale.ENGLISH)) {
            getSimulation().print("Reading text file : " + file + "\n");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                getSimulation().println(line);
            }
				getSimulation().println("");
		} catch (Exception e) {
            getSimulation().println(e);
        }
    }

}
