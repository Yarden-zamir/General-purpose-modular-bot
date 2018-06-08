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

    public static HashMap<String, moduleConfig> configs = new HashMap<>();

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

        //configs sutffs
        String title = configs.get("core").getConfigEntry("windowTitle", "Modular bot (root frame)");
        String winSizeStr = configs.get("core").getConfigEntry("windowSize", "900,600");
        mainWindow = new JFrame(title);
        mainWindow.setSize(
                Integer.parseInt(winSizeStr.split(",")[0]),
                Integer.parseInt(winSizeStr.split(",")[1])
        );
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        mainWindow.add(test);

    }

    //
    private static void loadConfigs(File cfgFile) {
        if (!configFileLoc.exists()){
            return;
        }
        String fullConfig = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(configFileLoc));
            String st;
            while ((st = br.readLine()) != null) {
                if (st.contains(":{")) {  //module found
                    String ModuleName = st.replace(":{", "");
                    moduleConfig mc = new moduleConfig(ModuleName);
                    //construct module config for the given module
                    st = br.readLine();
                    while (!st.contains("}")) {
                        if (st.contains(":")) {//checks that it's a field and not a comment
                            mc.addConfigEntry(st.split(":")[0].trim(), st.split(":")[1].trim());
                        } else {//it's a comment

                        }
                        st = br.readLine();
                    }
                    configs.put(ModuleName, mc);
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
