package com.rieke.bmore.catan.gui;

import com.rieke.bmore.catan.jetty.CatanJettyRunner;
import com.rieke.jettylauncher.JettyRunner;
import com.rieke.jettylauncher.gui.Startup;

import javax.swing.*;
import java.awt.*;

public class CatanStartup extends Startup {

    public static CatanJettyRunner jettyServer;

    public CatanStartup(JettyRunner jettyRunner) {
        super(jettyRunner);
    }

    @Override
    protected void init(JettyRunner jettyRunner, Container cp) {
        jettyServer = (CatanJettyRunner) jettyRunner;
        setTitle("Catan Server Launcher");
        super.init(jettyRunner,cp);
    }

    public static void main(String[] args) {
        // Run the GUI construction in the Event-Dispatching thread for thread-safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CatanStartup(new CatanJettyRunner()); // Let the constructor do the job
            }
        });
    }
}
