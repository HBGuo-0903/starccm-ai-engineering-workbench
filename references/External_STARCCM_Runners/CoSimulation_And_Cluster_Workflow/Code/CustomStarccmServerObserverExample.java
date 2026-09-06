/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import star.base.neo.NeoProperty;
import star.base.neo.ServerObserver;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CustomStarccmServerObserverExample extends StarMacro {

    private final String OUTPUT_OBSERVER_COMMAND = "ConsoleMessage";

    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();

        CustomServerObserver observer = new CustomServerObserver(new File(_sim.getSessionDir() + File.separator + "observerOutput.log"));

        _sim.getServer().addServerObserver(OUTPUT_OBSERVER_COMMAND, observer);
        _sim.getSimulationIterator().run(10);
        _sim.getServer().removeServerObserver(OUTPUT_OBSERVER_COMMAND, observer);
    }

}

class CustomServerObserver extends ServerObserver {

    private BufferedWriter _output;
    private final String MESSAGE_TAG = "message";

    CustomServerObserver(File output) {
        try {
            _output = new BufferedWriter(new FileWriter(output));
            writeFileHeader();
        } catch (IOException ex) {
        }
    }

    @Override
    public void finalize() {
        //make sure that the buffered write gets closed.
        try {
            _output.close();
            super.finalize();
        } catch (Exception ex) {
        } catch (Throwable ex) {
        } finally {
            try {
                _output.close();
            } catch (Exception ex) {

            }
        }
    }

    @Override
    protected void updateSpecific(NeoProperty np) {
        //User this method to direct the output.  Here we are sending it to a file.
        try {
            if (_output != null) {
                _output.write(np.getString(MESSAGE_TAG));
                _output.newLine();
                _output.flush();
            }
        } catch (IOException ex) {

        }

    }

    private void writeFileHeader() throws IOException {
        if (_output != null) {
            _output.write("Custom log output created by " + this.getClass().getSimpleName() + "\n");
            _output.flush();
        }
    }

}
