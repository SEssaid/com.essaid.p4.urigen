package com.essaid.p4.urigen.menu.action;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLWorkspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;
import com.essaid.p4.urigen.menu.TopMenu;
import com.essaid.p4.urigen.prefs.PrefsManager;

public class ActiveAction extends ProtegeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ActiveAction.class);

	private JCheckBoxMenuItem menuItem;
	private TopMenu top;

	@Override
	public void initialise() throws Exception {
		this.top = MenuManager.INSTANCE().getTopMenu(getEditorKit());
		top.setActiveAction(this);

		log.debug("Active action initialized.");

		final OWLWorkspace workspace = ((OWLEditorKit) getEditorKit())
				.getOWLWorkspace();

		final ComponentListener cl = new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				log.debug("Component shown event");

			}

			@Override
			public void componentResized(ComponentEvent e) {
				log.debug("Component resized event");

				OWLWorkspace ws = (OWLWorkspace) e.getSource();

				WorkspaceFrame frame = ProtegeManager.getInstance()
						.getFrame(ws);
				JMenuBar menubar = frame.getJMenuBar();

				int menuCount = menubar.getMenuCount();
				for (int i = 0; i < menuCount; ++i) {
					JMenu menu = menubar.getMenu(i);
					if (menu.getText().toLowerCase().startsWith("urigen")) {
						top.setTopMenu(menu);
						break;
					}
				}

				workspace.removeComponentListener(this);

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				log.debug("Component moved event");
				// TODO Auto-generated method stub

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				log.debug("Component hidden event");
				// TODO Auto-generated method stub

			}
		};

		workspace.addComponentListener(cl);

	}

	public void setMenuItem(JCheckBoxMenuItem item) {
		this.menuItem = item;
		boolean active = PrefsManager.INSTANCE().getMenuBooleanPreference(
				"active");
		if (active) {
			menuItem.doClick();
		}
	}

	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		log.debug("Menu item selected: {}", menuItem.isSelected());
		if (menuItem.isSelected()) {
			((OWLEditorKit) getEditorKit()).getOWLModelManager()
					.setOWLEntityFactory(
							MenuManager.INSTANCE().getEntityFactory(
									getEditorKit()));
			PrefsManager.INSTANCE().setMenuBooleanPreference("active", true);

		} else {
			((OWLEditorKit) getEditorKit()).getOWLModelManager()
					.setOWLEntityFactory(null);
			PrefsManager.INSTANCE().setMenuBooleanPreference("active", false);
		}

	}

	public boolean isActive() {
		return menuItem.isSelected();
	}
}
