/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules.guiModules;

import com.pandev.modularbot.modules.guiModule;
import javax.swing.JFrame;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 *
 * @author PandaBoy444
 */
public class mainGuiModule extends Plugin{
    
    public mainGuiModule(PluginWrapper wrapper) {
        super(wrapper);
    }
    
    @Extension
    public static class mainWindow implements guiModule{

        @Override
        public guiModule loadModule() {
            return this;
        }

        @Override
        public JFrame buildGui() {
            JFrame guiFrame = new JFrame("modularBot (GUI module)");
            guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            return guiFrame;
        }
        
    }
}
