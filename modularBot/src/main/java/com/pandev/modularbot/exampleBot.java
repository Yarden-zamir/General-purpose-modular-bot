/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot;

import com.pandev.modularbot.modules.chatModule;
import com.pandev.modularbot.modules.coreModule;
import com.pandev.modularbot.modules.module;
import com.pandev.modularbot.modules.moduleConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 *
 * @author PandaBoy444
 */
public class exampleBot extends Plugin {

    public exampleBot(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class github implements coreModule {

        @Override
        public module loadModule(moduleConfig cfg) {
            //

            //
            return this;
        }

    }

    @Extension
    public static class discord implements coreModule {

        @Override
        public module loadModule(moduleConfig cfg) {
            //
            try {
                GLOBAL.discordClient = new JDABuilder(AccountType.BOT).
                        setToken(cfg.getConfigEntry("botToken", "")).buildBlocking();
            } catch (LoginException | InterruptedException ex) {
                System.err.println(">>> ERR: can't connect to discord bot");
                Logger.getLogger(exampleBot.class.getName()).log(Level.SEVERE, null, ex);
            }
            //
            return this;
        }

    }

    @Extension
    public static class issueAdder implements chatModule {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
        }

        @Override
        public module loadModule(moduleConfig cfg) {
            //
            //
            return this;
        }

    }

}
