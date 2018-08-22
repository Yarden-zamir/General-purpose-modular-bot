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
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.SearchRepository;
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

    public static String commandPrefix;
    public static String permissions;
    public static String commandChannels;
    public static String seperator;

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
            GLOBAL.githubRepo = new SearchRepository(
                    cfg.getConfigEntry("repoOwner"),
                    cfg.getConfigEntry("repoName")
            );
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
    public static class Adapter extends chatModule {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            try {
                if (GLOBAL.isDebug) {
                    //only listen to debug channels
                    if (event.getTextChannel().getName().equals("debug") || event.getTextChannel().getName().equals("add-issue")) {

                        process(event);

                    }
                } else { //listen to all channels
                    process(event);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(core.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void process(MessageReceivedEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Method command = getCommand(event);
            if (command != null) {
                if (isPremitted(command, event.getMember().getRoles())) {

                    command.invoke(this, event);
                } else {
                    //maybe delete mesage?
                    event.getMessage().delete().queue();
                }
            }
        }

        private Method getCommand(MessageReceivedEvent event) {
            String messageText = event.getMessage().getContentStripped();
            //getting the list of commandChanels
            String[] commandChannelsArr = commandChannels.split(";");
            for (String c : commandChannelsArr) {
                if (c.split("~")[0].equals(event.getMessage().getChannel().getName())) {
                    messageText = core.commandPrefix + c.split("~")[1] + " " + messageText;
                    System.err.println(messageText);
                }
            }
            //
            for (Method m : this.getClass().getDeclaredMethods()) {
                if (messageText.startsWith(core.commandPrefix + m.getName())) {
                    System.err.println("match " + messageText);
                    for (Annotation a : m.getDeclaredAnnotations()) {
                        if (((command) a).callable()) {
                            return m;
                        }
                    }
                }
            }
            System.err.println("no command " + messageText + " =/= ");
            return null;
        }

        private boolean isPremitted(Method m, List<Role> roles) {
            String[] permsArr = permissions.split(";");
            for (String p : permsArr) {
                for (Role r : roles) {
                    if (p.contains(r.getName())) {
                        int permsLvl = Integer.parseInt(p.split("~")[1]);
                        for (Annotation a : m.getAnnotations()) {
                            if (((command) a).permissionLevel() <= permsLvl) {
                                return true;
                            }
                        }
                    }
                }
            }
            System.err.println("not allowed");
            return false;
        }

        private List<Issue> getOpenIssues() throws IOException {
            return GLOBAL.githubIssueService.getIssues(GLOBAL.githubRepo, null);
        }

        private List<Issue> getClosedIssues() throws IOException {
            Map<String, String> filter = new HashMap<>();
            filter.put("state", "closed");
            return GLOBAL.githubIssueService.getIssues(GLOBAL.githubRepo, filter);
        }

        private List<Issue> getAllIssues() throws IOException {
            List<Issue> out = new ArrayList<>();
            out.addAll(getClosedIssues());
            out.addAll(getOpenIssues());
            return out;
        }

        //
        public boolean isNum(String str) {
            return str.matches("-?\\d+(\\.\\d+)?");
        }

        //
        @command(permissionLevel = 0, callable = true,
                commandHelp = "@optional[command]  -> listst all commands and their help entries or prints help entries for a specific command")
        public void help(MessageReceivedEvent event) {
            String msg = event.getMessage().getContentStripped();
            if (msg.split(" ").length >= 2) {//specefied a command
                for (Method m : this.getClass().getDeclaredMethods()) {
                    if (m.getName().equals(msg.split(" ")[1].replaceFirst("/", ""))) {
                        if (m.isAnnotationPresent(command.class)) {
                            System.err.println(((command) m.getAnnotationsByType(command.class)[0]).commandHelp());
                            ((command) m.getAnnotationsByType(command.class)[0]).commandHelp();
                            //TODO print help
                        }
                    }
                }
            } else {//list all
                for (Method m : this.getClass().getDeclaredMethods()) {
                    if (m.isAnnotationPresent(command.class)) {
                        System.err.println(m.getName() + "  -   "
                                + ((command) m.getAnnotationsByType(command.class)[0]).commandHelp());
                        ((command) m.getAnnotationsByType(command.class)[0]).commandHelp();
                        //TODO print help
                    }
                }
            }
        }

        @command(permissionLevel = 2, callable = true,
                commandHelp = "[issue name] @optional[issue description]")
        public void issue(MessageReceivedEvent event) throws IOException {
            String msg = event.getMessage().getContentDisplay().replaceFirst(commandPrefix + "issue ", "");
            Issue newIssue = new Issue();
            newIssue.setTitle(msg.split(core.seperator)[0]);
            if (msg.split(core.seperator).length > 1) {
                newIssue.setBody(msg.split(core.seperator, 2)[1]);
            }
            GLOBAL.githubIssueService.createIssue(GLOBAL.githubRepo, newIssue);
        }

        @command(permissionLevel = 3, callable = true,
                commandHelp = "[issue id] [reason/comment to go with opening the issue] -> opens a closed issue with an optional comment to go along")
        public void open(MessageReceivedEvent event) throws IOException {
            String msg = event.getMessage().getContentStripped();
            String issueIdStr = msg.split(" ")[1];
            if (isNum(issueIdStr)) {
                int issueId = Integer.parseInt(issueIdStr);
                if (msg.split(" ").length > 2) {
                    GLOBAL.githubIssueService.createComment(GLOBAL.githubRepo, issueId, msg.split(" ", 3)[2]);
                }
                //TODO send opened
                GLOBAL.githubIssueService.editIssue(
                        GLOBAL.githubRepo,
                        GLOBAL.githubIssueService.getIssue(
                                GLOBAL.githubRepo, issueId
                        ).setState("open")
                );
            } else {
                //TODO send error
            }
        }

        @command(permissionLevel = 3, callable = true,
                commandHelp = "[issue id] [reason/comment to go with the close] -> closes an issue with an optional comment to go along")
        public void close(MessageReceivedEvent event) throws IOException {
            String msg = event.getMessage().getContentStripped();
            String issueIdStr = msg.split(" ")[1];
            if (isNum(issueIdStr)) {
                int issueId = Integer.parseInt(issueIdStr);
                if (msg.split(" ").length > 2) {
                    GLOBAL.githubIssueService.createComment(GLOBAL.githubRepo, issueId, msg.split(" ", 3)[2]);
                }
                //TODO send closed
                GLOBAL.githubIssueService.editIssue(
                        GLOBAL.githubRepo,
                        GLOBAL.githubIssueService.getIssue(
                                GLOBAL.githubRepo, issueId
                        ).setState("closed")
                );
            } else {
                //TODO send error
            }
        }

        @command(permissionLevel = 3, callable = true)
        public void assign(MessageReceivedEvent event) {

        }

        @command(permissionLevel = 3, callable = true)
        public void comment(MessageReceivedEvent event) throws IOException {
            String msg = event.getMessage().getContentStripped();
            if (msg.split(" ").length >= 2) {
                String issueIdStr = msg.split(" ")[1];
                if (isNum(issueIdStr)) {
                    int issueId = Integer.parseInt(issueIdStr);
                    if (msg.split(" ").length >= 2) {
                        GLOBAL.githubIssueService.createComment(GLOBAL.githubRepo, issueId, msg.split(" ", 2)[1]);
                    }
                } else {
                    //TODO send error
                }
            }
        }

        //
        @Override
        public module loadModule(moduleConfig cfg) {
            core.commandPrefix = cfg.getConfigEntry("commandPrefix");
            core.permissions = cfg.getConfigEntry("permissions");
            core.commandChannels = cfg.getConfigEntry("commandChannels");
            core.seperator = cfg.getConfigEntry("seperator");
            return this;
        }

    }

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface command {

    public boolean callable() default false;

    public int permissionLevel() default 0;

    public String commandHelp() default "No help provided, figure it out";

}
