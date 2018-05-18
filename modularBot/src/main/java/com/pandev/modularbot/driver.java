/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot;

import com.pandev.modularbot.modules.chatModule;
import com.pandev.modularbot.modules.guiModule;
import java.io.File;
import java.util.List;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

/**
 *
 * @author PandaBoy444
 */
public class driver {

    public static void main(String[] args) {
        if (args[0].contains("dev")){
            System.err.println(">>> entering dev mod");
        }
        PluginManager pluginManager = new DefaultPluginManager(new File("plugins").toPath());
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        
        loadChatModules(pluginManager);
        loadGuiModules(pluginManager);
    }

    private static void loadChatModules(PluginManager pluginManager) {
        List<chatModule> chatModules = pluginManager.getExtensions(chatModule.class);
        for (chatModule cm : chatModules) {
            System.out.println(">>> Started loading " + cm.getClass());
            cm.loadModule();
            System.out.println(">>> Finished loading " + cm.getClass() + "\n");
        }
    }

    private static void loadGuiModules(PluginManager pluginManager) {
        List<guiModule> guiModules = pluginManager.getExtensions(guiModule.class);
        for (guiModule gm : guiModules) {
            System.out.println(">>> Started loading " + gm.getClass());
            gm.loadModule();
            System.out.println(">>> Finished loading " + gm.getClass() + "\n");
        }
    }
}
