package com.essaid.p4.urigen.menu.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.protege.editor.core.ui.action.ProtegeDynamicAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;
import com.essaid.p4.urigen.menu.TopMenu;
import com.essaid.p4.urigen.prefs.Pref;
import com.essaid.p4.urigen.prefs.PrefsManager;

public class GeneratorAction extends ProtegeDynamicAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(GeneratorAction.class);
	private Pref selectedPreference;
	private Pref selectedPreferenceOld;
	private ActionListener itemListener;

	@Override
	public void initialise() throws Exception {
		String savedValue = PrefsManager.INSTANCE().getMenuPreference(
				"selected-pref-id");
		if (savedValue != null) {
			int id = Integer.valueOf(savedValue);
			selectedPreference = PrefsManager.INSTANCE().getPref(id);
		}

		this.itemListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Integer id = (Integer) ((JCheckBoxMenuItem) e.getSource())
						.getClientProperty("prefid");
				selectedPreference = PrefsManager.INSTANCE().getPref(id);
				PrefsManager.INSTANCE().setMenuPreference("selected-pref-id",
						"" + selectedPreference.getId());
				MenuManager.INSTANCE().getEntityFactory(getEditorKit()).setNeedsUpdate(true);
			}
		};

		TopMenu top = MenuManager.INSTANCE().getTopMenu(getEditorKit());
		top.setGeneratorAction(this);

	}

	@Override
	public void dispose() throws Exception {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug("Generator action performed with source id {} and event {}.",
				new Object[] { e.getSource().hashCode(), e });
	}

	@Override
	public void rebuildChildMenuItems(JMenu thisMenuItem) {
		boolean foundSelected = false;
		List<Pref> activePrefs = PrefsManager.INSTANCE().getSortedActivePrefs();
		List<JCheckBoxMenuItem> menuItems = new ArrayList<JCheckBoxMenuItem>();

		for (Pref pref : activePrefs) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(pref.getMenuName());
			item.addActionListener(itemListener);
			item.putClientProperty("prefid", pref.getId());
			if (pref == selectedPreference) {
				foundSelected = true;
				item.setSelected(true);
			}
			menuItems.add(item);
		}
		if (!foundSelected && selectedPreference != null) {
			selectedPreferenceOld = selectedPreference;
			selectedPreference = null;
		}
		for (JCheckBoxMenuItem item : menuItems) {
			thisMenuItem.add(item);
		}
	}

	public Pref getSelectedPref() {
		return selectedPreference;
	}
}
