package com.rieke.bmore.catan.jetty;

import com.rieke.jettylauncher.JettyRunner;

public class CatanJettyRunner extends JettyRunner {

    static {
        CONTEXT_PATH = "/catan";
    }

    @Override
    protected String getWebappPath() {
        return CatanJettyRunner.class.getClassLoader().getResource("webapp").toString();
    }

    @Override
    protected String getWebdefaultPath() {
        return CatanJettyRunner.class.getClassLoader().getResource("webapp/WEB-INF/webdefault.xml").toString();
    }
}