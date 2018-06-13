/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules.guiModules;

import com.pandev.modularbot.modules.frameModule;
import com.pandev.modularbot.modules.module;
import com.pandev.modularbot.modules.moduleConfig;
import javax.swing.JFrame;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 *
 * @author PandaBoy444
 */
public class exampleFrameModule extends Plugin {

    public exampleFrameModule(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class mainWindow implements frameModule {

        @Override
        public module loadModule(moduleConfig cfg) {
            return this;
        }

        @Override
        public JFrame buildFrame() {
            JFrame guiFrame = new JFrame("modularBot (GUI module)");
            guiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            guiFrame.setSize(500, 500);
            return guiFrame;
        }

    }
}
