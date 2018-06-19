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
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 *
 * @author PandaBoy444
 */
public class core extends Plugin {

    public static String commandPrefix = "";

    public core(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class github implements coreModule {

        @Override
        public module loadModule(moduleConfig cfg) {
            //
            GLOBAL.gitHubClient = new GitHubClient();
            GLOBAL.gitHubClient.setCredentials(
                    cfg.getConfigEntry("botUserName"),
                    cfg.getConfigEntry("botPassword")
            );
            GLOBAL.githubIssueService = new IssueService(GLOBAL.gitHubClient);
            core.commandPrefix = cfg.getConfigEntry("commandPrefix");
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
                Logger.getLogger(core.class.getName()).log(Level.SEVERE, null, ex);
            }
            //
            return this;
        }

    }

    @Extension
    public static class issueAdder extends chatModule {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            if (GLOBAL.isDebug) {
                //only listen to debug channels
                if (event.getTextChannel().getName().equals("debug")) {
                    process(event);
                }
            } else { //listen to all channels
                process(event);
            }

        }

        public void process(MessageReceivedEvent event) {
            if (event.getMessage().getContentStripped().startsWith(core.commandPrefix)) { // checks if it's a command
                for (Method m : this.getClass().getDeclaredMethods()) {
                    if (event.getMessage().getContentStripped().contains(m.getName())) {

                        try {
                            for (Annotation a : m.getDeclaredAnnotations()) {
                                boolean isCommand = a.toString().split("callable=")[1].split(",")[0].contains("true");
                                boolean isCommandAllowed = true;
                                if (isCommand && isCommandAllowed) {
                                    System.err.println("\nCalling " + m.getName() + "\n");
                                    m.invoke(this, event);
                                    return;
                                }
                            }
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(core.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        @command(permissionLevel = 2, callable = true)
        public void issue(MessageReceivedEvent event) {
            System.err.println("It works? " + event.getMessage().getContentStripped());
            java.awt.Toolkit.getDefaultToolkit().beep();
        }

        @Override
        public module loadModule(moduleConfig cfg) {
            return this;
        }

    }

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface command {

    public boolean callable() default false;

    public int permissionLevel() default 0;

}
