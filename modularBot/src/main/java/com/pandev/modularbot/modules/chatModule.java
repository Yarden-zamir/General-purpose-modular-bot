/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * a chat module is a module that receives chat events
 * @author PandaBoy444
 */
public abstract class chatModule extends ListenerAdapter implements module{
    /**
     * The receiver event, this will be called every time a discord message is received
     * @param event
     */
    @Override
    public abstract void onMessageReceived(MessageReceivedEvent event);
}
