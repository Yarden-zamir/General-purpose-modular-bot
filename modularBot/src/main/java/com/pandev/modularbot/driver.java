/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot;

import com.pandev.modularbot.modules.chatModule;
import com.pandev.modularbot.modules.frameModule;
import java.awt.EventQueue;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

/**
 *
 * @author PandaBoy444
 */
public class driver {

    private static JFrame mainWindow;
    public static void main(String[] args) {
        initConsoleWindow();
        
        //
        System.setProperty("pf4j.mode", "deployment");
        if (args[0].contains("dev")) {
            System.err.println(">>> entering dev mod");
        }
        PluginManager pluginManager = new DefaultPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        //
        loadChatModules(pluginManager);
        loadFrameModules(pluginManager);
        
        //
    }
    private static void initConsoleWindow(){
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

    private static void loadChatModules(PluginManager pluginManager) {
        System.out.println("There are " + pluginManager.getStartedPlugins().size() + " modules loaded");
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


class TextAreaOutputStream
        extends OutputStream {

// *************************************************************************************************
// INSTANCE MEMBERS
// *************************************************************************************************
    private byte[] oneByte;                                                    // array for write(int val);
    private Appender appender;                                                   // most recent action

    public TextAreaOutputStream(JTextArea txtara) {
        this(txtara, 1000);
    }

    public TextAreaOutputStream(JTextArea txtara, int maxlin) {
        if (maxlin < 1) {
            throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value=" + maxlin + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(txtara, maxlin);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    @Override
    public synchronized void close() {
        appender = null;
    }

    @Override
    public synchronized void flush() {
    }

    @Override
    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    @Override
    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    @Override
    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    static private String bytesToString(byte[] ba, int str, int len) {
        try {
            return new String(ba, str, len, "UTF-8");
        } catch (UnsupportedEncodingException thr) {
            return new String(ba, str, len);
        } // all JVMs are required to support UTF-8
    }

// *************************************************************************************************
// STATIC MEMBERS
// *************************************************************************************************
    static class Appender
            implements Runnable {

        private final JTextArea textArea;
        private final int maxLines;                                                   // maximum lines allowed in text area
        private final LinkedList<Integer> lengths;                                                    // length of lines within text area
        private final List<String> values;                                                    
        // values waiting to be appended

        private int curLength;                                                  // length of current line
        private boolean clear;
        private boolean queue;

        Appender(JTextArea txtara, int maxlin) {
            textArea = txtara;
            maxLines = maxlin;
            lengths = new LinkedList<>();
            values = new ArrayList<>();

            curLength = 0;
            clear = false;
            queue = true;
        }

        synchronized void append(String val) {
            values.add(val);
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        synchronized void clear() {
            clear = true;
            curLength = 0;
            lengths.clear();
            values.clear();
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
        @Override
        public synchronized void run() {
            if (clear) {
                textArea.setText("");
            }
            for (String val : values) {
                curLength += val.length();
                if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                    if (lengths.size() >= maxLines) {
                        textArea.replaceRange("", 0, lengths.removeFirst());
                    }
                    lengths.addLast(curLength);
                    curLength = 0;
                }
                textArea.append(val);
            }
            values.clear();
            clear = false;
            queue = true;
        }

        static private final String EOL1 = "\n";
        static private final String EOL2 = System.getProperty("line.separator", EOL1);
    }

} /* END PUBLIC CLASS */
