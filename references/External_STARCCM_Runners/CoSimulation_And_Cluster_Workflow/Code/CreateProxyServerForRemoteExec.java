/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.base.neo.NeoProperty;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CreateProxyServerForRemoteExec extends StarMacro {

    Simulation sim;

    @Override
    public void execute() {
        sim = getActiveSimulation();

        NeoProperty args = new NeoProperty();
        args.put("Host", "whitehead");
        args.put("ServerCommand", System.getProperty("star.serverCmd"));
        args.put("Verbose", 0);

        // spawn the proxy server
        NeoProperty response = getSimulation().execute("SpawnProxyServer", args);
        int port = response.getInt("Port");
        
        sim.println("Optimate spawned proxy server started at whitehead:" + port);
    }

}
