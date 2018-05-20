/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot;

import com.pandev.modularbot.modules.chatModule;
import com.pandev.modularbot.modules.frameModule;
import com.pandev.modularbot.modules.moduleConfig;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

/**
 *
 * @author PandaBoy444
 */
public class driver {

    /**
     * The file containing configs for all modules
     */
    public static File configFileLoc = new File("configs.cfg");

    private static JFrame mainWindow;

    private HashMap<String, moduleConfig> configs = new HashMap<>();

    public static void main(String[] args) {
        loadConfigs(configFileLoc);

        //
        initConsoleWindow();

        //
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        //
        loadChatModules(pluginManager);
        loadFrameModules(pluginManager);

        //
    }

    private static void initConsoleWindow() {
        JTextArea test = new JTextArea();
        TextAreaOutputStream console = new TextAreaOutputStream(test);
        PrintStream consoleStream = new PrintStream(console);

        System.setOut(consoleStream);
        System.setErr(consoleStream);

        mainWindow = new JFrame("Modular bot (root frame)");
        mainWindow.setSize(900, 600);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        mainWindow.add(test);

    }

    //
    private static void loadConfigs(File cfgFile) {
        String fullConfig = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(configFileLoc));
            String st;
            while ((st = br.readLine()) != null) {
                if (st.contains(":{")) {  //module found
                    String module = st.replace(":{", "");
                    //construct module config for the given module
                    while (!st.contains("}")) {
                        
                    }
                }
                fullConfig += st + "\n";
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(moduleConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(moduleConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(moduleConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //module loaders
    private static void loadChatModules(PluginManager pluginManager) {
        List<chatModule> chatModules = pluginManager.getExtensions(chatModule.class);
        System.err.println("There are " + chatModules.size() + " chat modules");
        chatModules.stream().map((cm) -> {
            cm.loadModule();
            return cm;
        }).forEachOrdered((cm) -> {
            System.out.println(">>> Finished loading " + cm.getClass().getCanonicalName() + "\n");
        });
    }

    private static void loadFrameModules(PluginManager pluginManager) {
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
            System.out.println("   >>> Finished building frame for " + gm.getClass().getCanonicalName() + "\n");
        });
    }
}
