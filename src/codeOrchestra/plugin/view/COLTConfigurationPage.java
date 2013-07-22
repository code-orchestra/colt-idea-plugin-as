package codeOrchestra.plugin.view;

import codeOrchestra.plugin.COLTSettings;

import javax.swing.*;

/**
 * @author Alexander Eliseyev
 */
public class COLTConfigurationPage {

    private JPanel mainPanel;

    private COLTSettings coltSettings;

    public COLTConfigurationPage(COLTSettings coltSettings) {
        this.coltSettings = coltSettings;
        this.mainPanel = new JPanel();
    }

    public JPanel getContentPane() {
        return mainPanel;
    }

    public void dispose() {
        // TODO: implement
    }
}
