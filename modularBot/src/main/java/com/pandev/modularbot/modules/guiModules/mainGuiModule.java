/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules.guiModules;

import javax.swing.JFrame;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import com.pandev.modularbot.modules.frameModule;

/**
 *
 * @author PandaBoy444
 */
public class mainGuiModule extends Plugin {

    public mainGuiModule(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class mainWindow implements frameModule {

        @Override
        public frameModule loadModule() {
            return this;
        }

        @Override
        public JFrame buildFrame() {
            JFrame guiFrame = new JFrame("modularBot (GUI module)");
            guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            guiFrame.setSize(500, 500);
            return guiFrame;
        }

    }
}

class moduleConfig {

    private void addConfigEntry(String entryName) {
        addConfigEntry("potato", "");
    }

  //  public void addConfigEntry(String entryName, int defaultValue) {


    public void addConfigEntry(String entryName, String defaultValue) {
        
    }

}
