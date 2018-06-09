/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules.chatModules;

import com.pandev.modularbot.driver;
import com.pandev.modularbot.gates;
import com.pandev.modularbot.modules.chatModule;
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
public class issueModule extends Plugin {

    public issueModule(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class issueAdder implements chatModule {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
        }

        @Override
        public chatModule loadModule() {
            //
            initDiscord(driver.configs.get("discord"));
            initGithub(driver.configs.get("github"));
            //
            return this;
        }

        //

        private void initDiscord(moduleConfig discordConfigs) {
            try {
                gates.discordClient = new JDABuilder(AccountType.BOT).
                        setToken(discordConfigs.getConfigEntry("botToken", "")).buildBlocking();
            } catch (LoginException | InterruptedException ex) {
                System.err.println(">>> ERR: can't connect to discord bot");
                Logger.getLogger(issueModule.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        private void initGithub(moduleConfig githubConfigs) {

        }
    }

}
