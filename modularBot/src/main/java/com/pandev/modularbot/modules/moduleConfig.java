/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules;

import java.util.HashMap;

/**
 *
 * @author PandaBoy444
 */
public class moduleConfig {

    HashMap<String, String> configs;
    public String moduleName;

    public moduleConfig(String moduleName) {
        this.moduleName = moduleName;
        configs = new HashMap<>();
    }

    public void addConfigEntry(String entryName, String value) {
        configs.put(entryName, value);
        System.err.println("added " + "<" + entryName + ", " + value + ">" + " to "+moduleName);
    }

}
