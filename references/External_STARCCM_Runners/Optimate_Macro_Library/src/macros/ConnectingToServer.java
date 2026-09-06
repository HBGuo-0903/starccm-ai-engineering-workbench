/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.PrintWriter;
import java.io.StringWriter;
import star.base.neo.NeoProperty;
import star.base.neo.ServerConnection;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class ConnectingToServer extends StarMacro {

    String host = "xeons24.adapco.com";
    int port = 47827;

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        try {
            ServerConnection sc = new ServerConnection(host, port);
            NeoProperty response = sc.getServerProxy().execute("GetExistingRootObject");
            if (!response.containsKey("RootObject")) {
                response = sc.getServerProxy().execute("GetTheRootObject");
            }
            Simulation foundSim = (Simulation) response.getObjectKey("RootObject", sc.getObjectRegistry()).getObject();
            sim.println(foundSim.getPresentationName());
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sim.println(sw.toString());
        }
    }

}
