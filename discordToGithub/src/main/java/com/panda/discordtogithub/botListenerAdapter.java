/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panda.discordtogithub;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;

/**
 *
 * @author PandaBoy444
 */
public class botListenerAdapter extends net.dv8tion.jda.core.hooks.ListenerAdapter {

    private GitHubClient github = null;
    private IssueService issueService = null;
    private String userName = "";
    private String password = "";
    private String repoPath = "";
    private String token = "";
    private String repoOwner = "";
    private final String seperator = "\n";
    private MessageChannel objMessageChannel;
    private JDA jdaBot;
    private MessageChannel outputChannel;
    private MessageChannel issueChatChannel;
    private String consoleChannelName = "";
    private String issueChatInpuNamet = "";
    private String issueOutpChannel = "";
    private ArrayList<String> backupList = new ArrayList<>();
    
    public botListenerAdapter(HashMap<String, String> entries) {
        this.userName = entries.get("S:botUserName").split(",")[0];
        this.password = entries.get("S:botPassword");
        this.repoPath = entries.get("S:repoPath").split(",")[0].split("/")[1];
        this.repoOwner = entries.get("S:repoPath").split(",")[0].split("/")[0];
        this.token = entries.get("S:botToken").split(",")[0];
        this.consoleChannelName = entries.get("S:issueInputChannel").split(",")[0];
        this.issueOutpChannel = entries.get("S:issueOutPutChannel").split(",")[0];
        this.issueChatInpuNamet = entries.get("S:issueCommentsChannel").split(",")[0];
        initDiscord(issueOutpChannel);
        initGithub();

        //
    }

    private void initDiscord(String outp) {
        try {
            jdaBot = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
            jdaBot.addEventListener(this);
            outputChannel = jdaBot.getTextChannelsByName(outp, true).get(0);
            outputChannel.getHistory().retrievePast(100).queue();
            issueChatChannel = jdaBot.getTextChannelsByName(issueChatInpuNamet, true).get(0);

        } catch (InterruptedException | LoginException e) {
            System.err.println("--Filed to authenticate bot");
        }
    }

    private void initGithub() {
        github = new GitHubClient();
        github.setCredentials(userName, password);
        issueService = new IssueService(github);
        System.out.println(github.getUser());
    }

    private void update(String msg) {
        try {
            issueService.createComment(repoOwner, repoPath, msg.split(" ")[0], msg.split(" ", 2)[2]);
        } catch (IOException ex) {
            objMessageChannel.sendMessage("cannot find issue").queue();
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void assign(String msg) {

    }

    public void proccessMessageText(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        if (msg.startsWith("/assign ")) {

        } else if (msg.startsWith("/update ")) {
            update(msg);
        } else if (msg.startsWith("/sync")) {
            syncFromHub();
        } else if (msg.startsWith("/show ")) {
            try {
                showIssue(issueService.getIssue(repoOwner, repoPath, msg.split(" ")[1]));
            } catch (IOException ex) {
                objMessageChannel.sendMessage("cannot find issue").queue();
                Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (msg.startsWith("/list")) {
            listIssues();
        } else if (msg.startsWith("/close ")) {
            closeIssue(msg);
        } else if (msg.startsWith("/help")) {
            printHelp();
        } else {
            addIssue(msg);
        }
    }

    public void showIssue(Issue I) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(I.getTitle(), I.getHtmlUrl());
        eb.setDescription(I.getBody());
        eb.addField("Number", "#" + I.getNumber(), true);
        eb.addField("Status", I.getState(), true);
        if (I.getState().equals("open")) {
            eb.setColor(Color.CYAN);
        } else {
            eb.setColor(Color.RED);
        }
        if (I.getLabels() != null) {
            String lbls = "```";
            lbls = I.getLabels().stream().map((L) -> "#" + L.getName() + "   ").reduce(lbls, String::concat);
            eb.addField("Lables", lbls + "```", true);
        }
        if (I.getAssignee() != null) {
            ArrayList<User> assigned = new ArrayList<>();
            eb.setFooter(I.getAssignee().getLogin(), I.getAssignee().getAvatarUrl());

            I.getUrl();
        }
        outputChannel.sendMessage(eb.build()).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        store(event);
        super.onMessageReceived(event);
        Message objMesasge = event.getMessage();
        objMessageChannel = event.getChannel();
        User objUser = event.getAuthor();
        if (!event.getAuthor().isBot()) {
            if (objMessageChannel.getName().equals(consoleChannelName)) {
                proccessMessageText(event);
            } else if (objMessageChannel.getName().equals(issueChatInpuNamet)) {
                commentOnIssue(event);
            }
        } else {
            //shold rename the title and chagne icon
            if (objMessageChannel.getName().equals(issueChatInpuNamet)) {
                if (event.getMessage().getEmbeds().size() > 0) {
                    MessageEmbed me = event.getMessage().getEmbeds().get(0);
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(me.getTitle(), me.getUrl());
                    String name = me.getDescription().split(":")[0].replace("@", "");
                    eb.setAuthor(name, "https://github.com/", jdaBot.getUsersByName(name, true).get(0).getAvatarUrl());
                    eb.setDescription(me.getDescription().split(":", 2)[1]);
                    eb.setColor(me.getColor());
                    event.getMessage().delete().queue();
                    issueChatChannel.sendMessage(eb.build()).queue();
                }
            }
        }
    }
    
   
   float lastTime = 0;
   float backupInterval = 1000;
   public void store(MessageReceivedEvent event){
        String data = event.getMessage().toString();
        backupList.add(data);
        System.err.println("data"+data);
   }

    private boolean done = false;

    public void commentOnIssue(MessageReceivedEvent event) {
        issueChatChannel.getHistory().retrievePast(100).queue();
        done = false;
        issueChatChannel.getIterableHistory().forEach((m) -> {
            if (!done) {
                if (m.getAuthor().toString().equals("U:GitHub(430382061392756736)")) {
                    String issueNumber = m.getEmbeds().get(0).getTitle().split("#")[1].split(":")[0];
                    System.out.println("------" + issueNumber);
                    String content = "@" + event.getAuthor().getName() + ": " + event.getMessage().getContentRaw();
                    event.getMessage().delete().queue();
                    try {
                        issueService.createComment(repoOwner, repoPath, issueNumber, content);
                    } catch (IOException ex) {
                        Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        objMessageChannel.sendMessage("Failed to comment on issue ").queue();
                    }
                    done = true;
                }
            }
        });
    }

    public Issue createIssue(Issue issue) {
        try {
            return issueService.createIssue(repoOwner, repoPath, issue);
        } catch (IOException ex) {
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
            objMessageChannel.sendMessage("Failed to add issue").queue();
        }
        return null;
    }

    private void syncFromHub() {
//        outputChannel.getHistory().retrievePast(10).queue();
//        outputChannel.deleteMessageById(outputChannel.getHistory().).queue();
        outputChannel.getIterableHistory().forEach((m) -> {
            outputChannel.deleteMessageById(m.getId()).queue();
        });
        try {
            for (Issue iss : Lists.reverse(getOpenIssues())) {
                showIssue(iss);
            }
        } catch (IOException ex) {
            objMessageChannel.sendMessage("Failed " + ex.getMessage()).queue();
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void listIssues() {
        try {
            if (getOpenIssues().isEmpty()) {
                objMessageChannel.sendMessage("Repo " + repoPath + " has " + 0 + " open issues").queue();
            }
        } catch (IOException ex) {
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String list = "";
            int i = getOpenIssues().size();
            objMessageChannel.sendMessage("Repo " + repoPath + " has " + i + " open issues").queue();
            int loops = 0;
            for (Issue iss : Lists.reverse(getOpenIssues())) {
                loops++;
                if (iss.getState().equals("open")) {
                    list += "[" + loops + "]: " + iss.getTitle() + " #" + iss.getNumber() + "\n";
                }
            }

            int charlimit = 1800;
            objMessageChannel.sendMessage("```css\n" + list.substring(0, Math.min(list.length(), charlimit)) + "```").queue();
            String listHold = list;
            int times = 1;
            while (listHold.length() > charlimit) {
                listHold = list.substring(charlimit * times).substring(0, Math.min(list.length(), charlimit));
                times++;
                objMessageChannel.sendMessage("```css\n" + listHold + "```").queue();
            }
        } catch (IOException ex) {
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
            objMessageChannel.sendMessage("Failed " + ex.getMessage()).queue();
        }
    }

    private void printHelp() {
        objMessageChannel.sendMessage("/close [issue id number] <-- close an exsisting issue"
                + "\n\n[issue title] <--add an issue "
                + "\n\n[issue title]\n[issue body] <--add an issue with a body"
                + "\n\n/list <-- lists all issues in repo").queue();
    }

    private List<Issue> getOpenIssues() throws IOException {
        return issueService.getIssues(repoOwner, repoPath, null);
    }

    private List<Issue> getClosedIssues() throws IOException {
        Map<String, String> filter = new HashMap<>();
        filter.put("state", "closed");
        return issueService.getIssues(repoOwner, repoPath, filter);
    }

    private List<Issue> getAllIssues() throws IOException {
        List<Issue> out = new ArrayList<>();
        out.addAll(getClosedIssues());
        out.addAll(getOpenIssues());
        return out;
    }

    private void closeIssue(String msg) {
        if (Character.isDigit(msg.replaceFirst("/close ", "").charAt(0))) {
            String[] parts = msg.split(" ", 3);
            int index = Integer.decode(parts[1]);
            try {
                System.out.println("Searching for " + index);
                boolean found = false;
                for (Issue iss : getOpenIssues()) {
                    found = true;
                    if (iss.getNumber() == index) {
                        System.out.println("Found!");
                        if (parts.length > 2) {
                            issueService.createComment(repoOwner, repoPath, index, parts[2]);
                        }
                        objMessageChannel.sendMessage("Closed:   " + iss.getTitle()).queue();
                        issueService.editIssue(repoOwner, repoPath, iss.setState("closed"));
                        for (Message m : outputChannel.getIterableHistory()) {
                            if (m.getEmbeds().get(0).getFields().get(0).equals(index)) {
//                                objMessageChannel.sendMessage("I can remove it if you want").queue();
                            }
                        }
                        break;
                    }
                }
                if (!found) {
                    objMessageChannel.sendMessage("Issue not found").queue();
                }
//                issueService.editIssue(repoOwner, repoPath, issueService.getIssue(repoOwner, repoPath, index).setState("closed"));
            } catch (IOException ex) {
                Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
                objMessageChannel.sendMessage("Issue not found").queue();
            }
        } else if (msg.contains("last")) {
            closeLast(msg);
        } else if (msg.contains("all")) {
            try {
                closeLast("/close last " + getOpenIssues().size());
            } catch (IOException ex) {
                Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
            syncFromHub();
        }

    }

    public void closeLast(String msg) {
        if (isNumeric(msg.replaceFirst("/close last ", ""))) {
            int a = Integer.parseInt(msg.replaceFirst("/close last ", ""));
            if (a > 1) {
                closeLast("/close last " + (a - 1));
            }
        }
        try {
            int max = getOpenIssues().get(0).getNumber();
            for (Issue temp : getOpenIssues()) {
                if (temp.getNumber() > max) {
                    max = temp.getNumber();
                }
            }
            Issue t = issueService.getIssue(repoOwner, repoPath, max);
            issueService.editIssue(repoOwner, repoPath, t.setState("closed"));
        } catch (IOException ex) {
            objMessageChannel.sendMessage("Could not find last issue").queue();
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    public void addIssue(String msg) {
        ArrayList<Label> labels = new ArrayList<>();
        LabelService ls = new LabelService(github);
        MilestoneService Ms = new MilestoneService(github);
        Issue newOne = new Issue();

        String milestonePrefix = "\\";
        if (msg.contains(milestonePrefix)) {
//            for (Ms.getMilestones(msg, repoPath, token))
        }
        String tagPrefix = "$";
        String tags;
        if (msg.contains(tagPrefix)) {
            msg = msg.split(tagPrefix, 2)[0];
            tags = msg.split(tagPrefix, 2)[1];
            System.out.println("");
            try {
                for (String tag : tags.split(tagPrefix)) {
                    System.out.println("Tag: " + tag);
                    for (Label lbl : ls.getLabels(repoOwner, repoPath)) {
                        if (lbl.getName().equals(tag)) {
                            labels.add(ls.getLabel(repoOwner, repoPath, tag));
                        } else {
                            ls.createLabel(repoOwner, repoPath, new Label().setName(tag));
                            labels.add(ls.getLabel(repoOwner, repoPath, tag));
                        }
                    }
                }
            } catch (IOException ex) {
                objMessageChannel.sendMessage("Error while adding tag").queue();
                Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (msg.contains(seperator)) {
            newOne.setBody(msg.split(seperator, 2)[1]);
        }
        newOne.setTitle(msg.split(seperator, 2)[0]);
        newOne = createIssue(newOne);
        try {
            ls.setLabels(repoOwner, repoPath, newOne.getNumber() + "", labels);
            showIssue(issueService.getIssue(repoOwner, repoPath, newOne.getNumber()));
        } catch (IOException ex) {
            objMessageChannel.sendMessage("Failed to set tag").queue();
            Logger.getLogger(botListenerAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
