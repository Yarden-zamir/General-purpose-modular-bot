/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panda.discordtogithub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author PandaBoy444
 */
public class Main {

    /**
     * The file with the authentication information for the discord bot and the
     * github dummy account
     */
    public static final File AUTH_FILE = new File("ath.cfg");

    /**
     * The main method takes the info in AUTH_FILE and creates a new
     * botListenerAdapter with it
     *
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            BufferedReader r = new BufferedReader(new FileReader(AUTH_FILE));
            //botListenerAdapter adp = new botListenerAdapter(r.readLine(), r.readLine(), r.readLine(), r.readLine(), r.readLine(), r.readLine());
//            ArrayList<ArrayList<String>> entries = new ArrayList<>();
            HashMap<String, String> entries = new HashMap<>();
            entries.put("S:botUserName", "");
            entries.put("S:botPassword", "");
            entries.put("S:repoPath", "");
            entries.put("S:botToken", "");
            entries.put("S:issueOutPutChannel", "");
            entries.put("S:issueInputChannel", "");
            entries.put("S:issueCommentsChannel", "");
            String line = r.readLine();
            while (!line.equals("}")){
                String key = line.trim().split("=")[0];
                if (entries.containsKey(key)){
                    entries.put(key, line.trim().split("=",2)[1]);
                }
                line = r.readLine();
            }
            botListenerAdapter adp = new botListenerAdapter(entries);
        } catch (FileNotFoundException ex) {
            generateFile();
            System.err.println("--File not found");
            new Scanner(System.in).nextLine();
        } catch (IOException ex) {
            System.err.println("--Error parsing file");
            new Scanner(System.in).nextLine();
        }

    }

    private static void generateFile() {

    }
}
