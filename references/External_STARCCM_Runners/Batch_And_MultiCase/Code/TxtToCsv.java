import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;


public class TxtToCsv {
	public static void main(String[] args) throws FileNotFoundException {
		//Scanner console = new Scanner(System.in);
		//System.out.println("What is the project file name?");
		//String fileName = console.next();
		String fileName = "C:\\Users\\tjp644\\Documents\\NetBeansProjects\\MyStarProject\\src\\macro\\FS_GSI_revQ_a10b0";
		String reports[]={fileName + "_CDWA.txt", 
		fileName + "_CMWA.txt", fileName + "_CNWA.txt",
		fileName + "_CRWA.txt"};

		
		for (String files : reports) {
			Scanner textFile = new Scanner(new File(files));
			PrintStream output = new PrintStream(new File(files.substring(0, files.length()-3) + "csv"));
			String line = textFile.nextLine();
			while(!line.startsWith("Part")){
				line=textFile.nextLine();
			}
			
			output.println(files.substring(0, files.length()-4));
			output.println();
			
			while(textFile.hasNextLine()&&!line.startsWith("Component")) {
				if(line.startsWith("Part")) {
					output.println(" , , Pressure, , , Shear, , , Net");
					output.println("Part, X, Y, Z, X, Y, Z, X, Y, Z");
				}
				else if(!line.startsWith("--")){
					char b = '[';
					char c = ',';
					char cb = ']';
					char e =  ' ';
					String line1 = line.replace(b,c);
					String line2 = line1.replace(cb,e);
					Scanner lineScanner = new Scanner(line2);
					while(lineScanner.hasNext()) {
						String token = lineScanner.next();
						output.print(token);
					}
					output.println();
				}
				line = textFile.nextLine();
			}
		}
	}
}		
