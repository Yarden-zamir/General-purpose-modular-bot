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
    }

    public String getConfigEntry(String entryName, String entryDefeaultValue) {
        if (configs.containsKey(entryName)) {
            return configs.get(entryName);
        }
        writeEntryToFile(entryName, entryDefeaultValue);
        return entryDefeaultValue;
    }

    public String getConfigEntry(String entryName) {
        return getConfigEntry(entryName, "null");
    }

    private void writeEntryToFile(String entryName, String value) {

    }

}
