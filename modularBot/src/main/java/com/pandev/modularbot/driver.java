/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot;

import com.pandev.modularbot.modules.chatModule;
import java.util.List;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import com.pandev.modularbot.modules.frameModule;

/**
 *
 * @author PandaBoy444
 */
public class driver {

    public static void main(String[] args) {
        if (args[0].contains("dev")){
            System.err.println(">>> entering dev mod");
        }
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        
        //
        
        loadChatModules(pluginManager);
        loadGuiModules(pluginManager);
    }

    private static void loadChatModules(PluginManager pluginManager) {
        System.out.println("There are "+pluginManager.getStartedPlugins().size()+" modules loaded");
        List<chatModule> chatModules = pluginManager.getExtensions(chatModule.class);
        System.err.println("There are "+chatModules.size()+" chat modules");
        chatModules.stream().map((cm) -> {
            cm.loadModule();
            return cm;
        }).forEachOrdered((cm) -> {
            System.out.println(">>> Finished loading " + cm.getClass().getCanonicalName() + "\n");
        });
    }

    private static void loadGuiModules(PluginManager pluginManager) {
        List<frameModule> guiModules = pluginManager.getExtensions(frameModule.class);
        guiModules.stream().map((gm) -> {
            gm.loadModule();
            return gm;
        }).map((gm) -> {
            System.out.println(">>> Finished loading " + gm.getClass().getCanonicalName() + "\n");
            return gm;
        }).map((gm) -> {
            gm.buildFrame().setVisible(true);
            return gm;
        }).forEachOrdered((gm) -> {
            System.out.println("   >>> Finished building frame for "+gm.getClass().getCanonicalName()+"\n");
        });
    }
}
