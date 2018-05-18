/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.pf4j.ExtensionPoint;

/**
 *
 * @author PandaBoy444
 */
public interface chatModule extends ExtensionPoint{

    /**
     * The init code for the module
     * @return itself
     */
    public chatModule loadModule();

    /**
     * The receiver event, this will be called every time a discord message is received
     * @param event
     */
    public void onMessageReceived(MessageReceivedEvent event);
}
