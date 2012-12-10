package com.essaid.p4.urigen.prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefsManager {

	private static Logger log = LoggerFactory.getLogger(PrefsManager.class);
	private static PrefsManager instance = null;

	private PrefsStore store;
	private Set<PrefsListener> listeners = new HashSet<PrefsListener>();

	public static PrefsManager INSTANCE() {
		if (instance == null) {
			log.debug("Instance created.");
			instance = new PrefsManager();
			instance.setPrefsStore(PrefsStore.INSTANCE());
			PrefsStore.INSTANCE().setPrefsManager(instance);
			instance.loadPreferences();

		}
		return instance;
	}

	void setPrefsStore(PrefsStore store) {
		log.debug("PrefsStore {} being set", store);
		this.store = store;
	}

	public boolean addPrefsListener(PrefsListener listener) {
		log.debug("PrefsListener {} being added", listener);
		return listeners.add(listener);
	}

	public boolean removePrefsListener(PrefsListener listener) {
		log.debug("PrefsListener {} being removed", listener);
		return listeners.remove(listener);
	}

	public Pref createNewPref() {
		log.debug("New preference being created");
		return new Pref(this);
	}

	public void save(Pref pref) throws PrefException {
		log.debug("Preference {} being saved", pref.toStringFull());
		PrefState state = pref.getState();

		if (store.savePref(pref)) {
			for (PrefsListener l : new HashSet<PrefsListener>(listeners)) {
				if (state == PrefState.NEW) {
					log.debug(
							"Prefs Listener {} is being notified about adding a new ref {}",
							new Object[] { l, pref.toStringFull() });
					l.prefAdded(pref);
				} else {
					log.debug(
							"Prefs Listener {} is being notified about changed ref {}",
							new Object[] { l, pref.toStringFull() });
					l.prefChanged(pref);
				}
			}
		}
	}

	public void delete(Pref pref) throws PrefException {
		log.debug("Preference {} is being deleted", pref.toStringFull());
		if (store.deletePref(pref)) {
			for (PrefsListener l : new HashSet<PrefsListener>(listeners)) {
				log.debug(
						"Prefs Listener {} is being notified about delted ref {}",
						new Object[] { l, pref.toStringFull() });
				l.prefRemoved(pref);
			}
		}
	}

	public Pref getPref(int id) {
		return store.getPref(id);
	}

	public List<Pref> getSortedActivePrefs() {
		log.debug("Getting active preferences");
		List<Pref> prefs = new ArrayList<Pref>();
		for (Pref p : store.getPrefs()) {
			if (p.isActive()) {
				prefs.add(p);
			}
		}
		Comparator<Pref> c = new Comparator<Pref>() {

			@Override
			public int compare(Pref p1, Pref p2) {
				return p1.getMenuName().compareToIgnoreCase(p2.getMenuName());
			}
		};
		Collections.sort(prefs, c);
		return prefs;
	}

	public List<Pref> getSortedAllPrefs() {
		log.debug("Getting all preferences");
		List<Pref> prefs = new ArrayList<Pref>(store.getPrefs());
		Comparator<Pref> c = new Comparator<Pref>() {

			@Override
			public int compare(Pref p1, Pref p2) {
				return p1.getId() - p2.getId();
			}
		};
		Collections.sort(prefs, c);
		return prefs;
	}

	public boolean getMenuBooleanPreference(String name) {
		return store.getMenuBooleanPreference(name);
	}

	public void setMenuBooleanPreference(String name, boolean value) {
		store.setMenuBooleanPreference(name, value);
	}

	public String getMenuPreference(String name) {
		return store.getMenuPreference(name);
	}

	public void setMenuPreference(String name, String value) {
		store.setMenuPreference(name, value);
	}

	private void loadPreferences() {
		log.debug("Loading preferences");
		store.loadPrefs();
		for (Pref p : store.getPrefs()) {
			for (PrefsListener l : new HashSet<PrefsListener>(listeners)) {
				l.prefAdded(p);
			}

		}

	}
}
