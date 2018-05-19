/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules;

import javax.swing.JPanel;

/**
 *
 * @author PandaBoy444
 */
public interface guiComponentModule extends module{
    public JPanel modulePanel=new JPanel();
    public void constructPanel();
}
