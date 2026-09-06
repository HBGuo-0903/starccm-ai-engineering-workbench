/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

//import core.Config;
import core.apiFramework.Exceptions.DesignSetLimitExceededException;
import core.apiFramework.OptimateProject;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class SaveProject extends StarMacro {

    @Override
    public void execute() {
//        OptimateProject proj = Config.getActiveProject();
//        
//        if (proj == null) {
//            getActiveSimulation().println("was null");
//            return;
//        }
//        
//        try {
//            proj.saveAs(new File(getActiveSimulation().getSessionDir() + File.separator + "oFiles/opt1.optm"));
//        } catch (DesignSetLimitExceededException ex) {
//            getActiveSimulation().println(ex.getLocalizedMessage());
//        } catch (IOException ex) {
//            getActiveSimulation().println(ex.getLocalizedMessage());
//        } catch (InterruptedException ex) {
//            getActiveSimulation().println(ex.getLocalizedMessage());
//        } catch (BackingStoreException ex) {
//            getActiveSimulation().println(ex.getLocalizedMessage());
//        }
    }
}
