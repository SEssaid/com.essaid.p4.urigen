package com.essaid.p4.urigen.menu.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;
import com.essaid.p4.urigen.menu.TopMenu;
import com.essaid.p4.urigen.prefs.PrefsManager;

public class FollowAction extends ProtegeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(FollowAction.class);
	private TopMenu top;
	private JCheckBoxMenuItem menuItem;

	@Override
	public void initialise() throws Exception {
		this.top = MenuManager.INSTANCE().getTopMenu(getEditorKit());
		top.setFollowAction(this);

		log.debug("Follow action initialized.");

	}

	public void setMenuItem(JCheckBoxMenuItem item) {
		this.menuItem = item;
		boolean follow = PrefsManager.INSTANCE().getMenuBooleanPreference(
				"follow");
		if (follow) {
			menuItem.doClick();
		}
	}

	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug("Follow action performed with source id {} and event {}.",
				new Object[] { e.getSource().hashCode(), e });
		if (menuItem.isSelected()) {
			PrefsManager.INSTANCE().setMenuBooleanPreference("follow", true);
		} else {
			PrefsManager.INSTANCE().setMenuBooleanPreference("active", false);
		}

	}

	public boolean isFollow() {
		return menuItem.isSelected();
	}

}
