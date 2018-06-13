/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules.core;

import com.pandev.modularbot.modules.coreModule;
import com.pandev.modularbot.modules.module;
import com.pandev.modularbot.modules.moduleConfig;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 *
 * @author PandaBoy444
 */
public class discordModules extends Plugin {

    public discordModules(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class discordLoader implements coreModule {

        @Override
        public module loadModule(moduleConfig cfg) {
            //
            //
            return this;
        }

    }

}
