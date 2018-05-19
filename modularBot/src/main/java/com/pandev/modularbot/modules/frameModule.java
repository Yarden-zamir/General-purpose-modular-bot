/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pandev.modularbot.modules;

import javax.swing.JFrame;

/**
 *
 * @author PandaBoy444
 */
public interface frameModule extends module {
    /**
     * Builds the gui for this module
     * @return A JFrame representing this module
     */
    public JFrame buildFrame();
}
