/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modulardiscordbot;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 *
 * @author PandaBoy444
 */
public class botDriver {
    private static File configFile = new File("configs.cfg");
    public static void main(String[] args) {
        System.out.println("initializing");
        new configLoader().loadConfigs(configFile);
    }
}

class pluginLoader {

    public boolean loadPlugins(ArrayList<URI> pluginLocations) {
        return true;
    }

}

class configLoader {

    public void loadConfigs(File configLocaion) {
        try {
            JsonReader read = new JsonReader(new FileReader(configLocaion));
            Gson config = new Gson();
        } catch (IOException e) {
            System.err.println("Can't read configs \n"+e.getLocalizedMessage());
        }
    }
}

class configs {
    private ArrayList<String> pluginLoc;

    public configs() {
        pluginLoc = new ArrayList<String>();
    }
    
}
