/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc.logging;

import java.util.logging.*;

/**
 *
 * @author aarong
 */
public class WorkerClass {
    
    private static final Logger LOGGER = Utils.configureLogger(Logger.getLogger(WorkerClass.class.getName()));
  
    void do1() {
        LOGGER.severe("do 1 severe");
        LOGGER.warning("do 1 warning");
        LOGGER.info("do 1 info");
        LOGGER.config("do 1 config");
        LOGGER.fine("do 1 fine");
        LOGGER.finer("do 1 finer");
        LOGGER.finest("do 1 finest");        
    }
    
    void do2() {
        LOGGER.info("do 2");
    }
    
    
}
