package com.essaid.p4.urigen.prefs.ui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.essaid.p4.urigen.prefs.Pref;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class NewPrefPanel {

	JPanel panel;
	JCheckBox active;
	JTextField menuName;
	JTextField ontologyIri;
	JTextField serverUrl;
	JTextField serverKey;
	JTextField comment;

	public NewPrefPanel() {
		panel = new JPanel();
		panel.setLayout(new MigLayout(new LC().fill().width("200")));

		panel.add(new JLabel("Menu name:"));
		panel.add(menuName = new JTextField(), new CC().wrap().growX().maxWidth("500"));

		panel.add(new JLabel("Ontology Iri:"));
		panel.add(ontologyIri = new JTextField(), new CC().wrap().growX().maxWidth("500"));

		panel.add(new JLabel("Server URL:"));
		panel.add(serverUrl = new JTextField(), new CC().wrap().growX().maxWidth("500"));

		panel.add(new JLabel("Server key:"));
		panel.add(serverKey = new JTextField(), new CC().wrap().growX().maxWidth("500"));

		panel.add(new JLabel("Comment:"));
		panel.add(comment = new JTextField(), new CC().wrap().growX().maxWidth("500"));

		panel.add(new JLabel("Active:"));
		panel.add(active = new JCheckBox(), new CC().growX().maxWidth("500"));

		panel.validate();
	}

	void populatePref(Pref pref) {
		pref.setActive(active.isSelected());
		pref.setMenuName(menuName.getText());
		pref.setOntologyIri(ontologyIri.getText());
		pref.setServerUrl(serverUrl.getText());
		pref.setServerKey(serverKey.getText());
		pref.setComment(comment.getText());
	}

	public JComponent getPanel() {
		return panel;
	}
}
