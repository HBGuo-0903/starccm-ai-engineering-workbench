/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;

import java.io.PrintWriter;
import java.io.StringWriter;
import star.common.Simulation;

/**
 *
 * @author aarong
 */
public class Utilities {
    
    public static void printStackTrace(Exception ex, Simulation sim) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        sim.println(sw.toString());
    }
}
