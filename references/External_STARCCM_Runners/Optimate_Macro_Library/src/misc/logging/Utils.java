/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author aarong
 */
public class Utils {

    static Level level1 = Level.CONFIG;
    static Level level2 = Level.FINE;

    public static Logger configureLogger(Logger logger) {
        logger.setUseParentHandlers(false);
        logger.setLevel(level2);

        ConsoleHandler console = null;
        FileHandler fileXML;
        FileHandler fileTxt;

        for (Handler h : logger.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                console = (ConsoleHandler) h;
                console.setLevel(level1);
            } else if (h instanceof FileHandler) {
                logger.removeHandler(h);
            }
        }

        if (console == null) {
            console = new ConsoleHandler();
            console.setLevel(level1);
            logger.addHandler(console);
        }

        String userHome = System.getProperty("user.home");
        try {
            fileXML = new FileHandler(userHome + File.separator + ".optlogXML");
            fileXML.setLevel(level2);
            logger.addHandler(fileXML);
            fileTxt = new FileHandler(userHome + File.separator + ".optlogTXT");
            SimpleFormatter formatter = new SimpleFormatter();
            fileTxt.setFormatter(formatter);
            logger.addHandler(fileTxt);
        } catch (IOException ex) {
        } catch (SecurityException ex) {
        }

        return logger;
    }

}
