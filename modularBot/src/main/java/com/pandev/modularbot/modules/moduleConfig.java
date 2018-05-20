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

    private void addConfigEntry(String entryName) {
        addConfigEntry(entryName, "");
    }

    public void addConfigEntry(String entryName, String defaultValue) {
        configs.put(entryName, defaultValue);
    }


}


