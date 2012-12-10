package com.essaid.p4.urigen.prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class PrefsStore {

	public static final String URIGEN_ACTIVE = "_active_";

	public static final String URIGEN_COMMENT = "_comment_";
	public static final String URIGEN_MENU_NAME = "_menuname_";
	public static final String URIGEN_ONTOLOGY_IRI = "_iri_";
	public static final String URIGEN_PREFS_SET = "com.essaid.p4.urigen.prefs";

	public static final String URIGEN_PREFS_SET_IDS = "ids";
	public static final String URIGEN_PREFS_SET_IDS_COUNT = "count";
	public static final String URIGEN_PREFS_SET_MENU = "menu";
	public static final String URIGEN_PREFS_SET_VALUES = "values";
	public static final String URIGEN_SERVER_KEY = "_skey_";
	public static final String URIGEN_SERVER_URL = "_surl_";
	private static PrefsStore store = null;

	public static PrefsStore INSTANCE() {
		if (store == null) {
			store = new PrefsStore();
		}
		return store;
	}

	private PreferencesManager protegePrefsManager = PreferencesManager
			.getInstance();

	private Map<Integer, Pref> prefsMap = new HashMap<Integer, Pref>();

	private int prefsCount = 0;

	private PrefsManager prefsManager;

	private PrefsStore() {

	}

	boolean deletePref(Pref pref) throws PrefException {

		boolean prefDeleted = false;

		switch (pref.getState()) {
		case NEW:
			break;

		case DELETED:
			throw new PrefException("A deleted preference being deleted again.");

		case CLEAN:
		case DIRTY:
			prefsMap.remove(pref.getId());
			int id = pref.getId();
			Preferences preferences = getPreferences(URIGEN_PREFS_SET_VALUES);
			preferences.putString(id + URIGEN_ACTIVE, null);
			preferences.putString(id + URIGEN_COMMENT, null);
			preferences.putString(id + URIGEN_MENU_NAME, null);
			preferences.putString(id + URIGEN_ONTOLOGY_IRI, null);
			preferences.putString(id + URIGEN_SERVER_KEY, null);
			preferences.putString(id + URIGEN_SERVER_URL, null);
			--prefsCount;
			updateIdCounts();
			pref.setState(PrefState.DELETED);
			prefDeleted = true;
		}

		return prefDeleted;
	}

	/**
	 * Returns a {@link Pref} with the id assigned;
	 * 
	 * @param pref
	 * @return
	 */
	boolean savePref(Pref pref) throws PrefException {
		boolean prefSaved = false;
		switch (pref.getState()) {

		case CLEAN:
			break;

		case DELETED:
			throw new PrefException(
					"Preference being saved after being deleted.");

		case NEW:
			// check before accepting a new pref
			if (getPrefWithMenuNameIgnoreCase(pref) != null) {
				throw new PrefException(
						"A preference with similar menu name exists");
			}
			pref.setState(PrefState.DIRTY);
			pref.setId(getNextValidId());
			prefsMap.put(pref.getId(), pref);
			++prefsCount;
			updateIdCounts();
		case DIRTY:
			// check if a dirty pref violates this constraint
			if (getPrefWithMenuNameIgnoreCase(pref) != null) {
				throw new PrefException(
						"A preference with similar menu name exists");
			}
			Preferences prefs = getPreferences(URIGEN_PREFS_SET_VALUES);
			prefs.putBoolean(pref.getId() + URIGEN_ACTIVE, pref.isActive());
			prefs.putString(pref.getId() + URIGEN_COMMENT, pref.getComment());
			prefs.putString(pref.getId() + URIGEN_MENU_NAME, pref.getMenuName());
			prefs.putString(pref.getId() + URIGEN_ONTOLOGY_IRI,
					pref.getOntologyIri());
			prefs.putString(pref.getId() + URIGEN_SERVER_URL,
					pref.getServerUrl());
			prefs.putString(pref.getId() + URIGEN_SERVER_KEY,
					pref.getServerKey());
			pref.setState(PrefState.CLEAN);
			prefSaved = true;
		}

		return prefSaved;
	}

	List<Pref> getPrefs() {
		return new ArrayList<Pref>(prefsMap.values());
	}

	Pref getPref(int id) {
		return prefsMap.get(id);
	}

	private Pref getPrefWithMenuNameIgnoreCase(Pref pref) {
		for (Pref p : prefsMap.values()) {
			if (p == pref) {
				continue;
			}
			if (p.getMenuName().equalsIgnoreCase(pref.getMenuName())) {
				return p;
			}
		}
		return null;
	}

	private int getNextValidId() {
		int id = -1;
		List<Integer> orderedIds = new ArrayList<Integer>(prefsMap.keySet());
		Collections.sort(orderedIds);
		int counter = 0;
		for (int i : orderedIds) {
			if (i > counter) {
				id = counter;
				break;
			}
			++counter;
		}
		if (id == -1) {
			id = counter;
		}
		return id;
	}

	private Preferences getPreferences(String id) {
		return protegePrefsManager.getPreferencesForSet(URIGEN_PREFS_SET, id);
	}

	private void updateIdCounts() {
		if (prefsCount != prefsMap.keySet().size()) {
			throw new RuntimeException("Prefs count doesn't match map size.");
		}
		Preferences prefs = getPreferences(URIGEN_PREFS_SET_IDS);
		prefs.clear();
		int counter = 0;
		for (int i : prefsMap.keySet()) {
			prefs.putInt("" + counter, i);
			++counter;
		}
		prefs.putInt(URIGEN_PREFS_SET_IDS_COUNT, counter);
	}

	void loadPrefs() {
		Preferences prefIds = getPreferences(URIGEN_PREFS_SET_IDS);
		prefsCount = prefIds.getInt(URIGEN_PREFS_SET_IDS_COUNT, 0);
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < prefsCount; ++i) {
			int id = prefIds.getInt("" + i, -1);
			// TODO Check for -1
			ids.add(id);
		}
		Preferences prefPreferences = getPreferences(URIGEN_PREFS_SET_VALUES);
		for (int id : ids) {
			Pref pref = prefsManager.createNewPref();
			pref.setId(id);
			pref.setActive(prefPreferences
					.getBoolean(id + URIGEN_ACTIVE, false));
			pref.setMenuName(prefPreferences.getString(id + URIGEN_MENU_NAME,
					"DEFAULT"));
			pref.setOntologyIri(prefPreferences.getString(id
					+ URIGEN_ONTOLOGY_IRI, "DEFAULT"));
			pref.setServerUrl(prefPreferences.getString(id + URIGEN_SERVER_URL,
					"DEFAULT"));
			pref.setServerKey(prefPreferences.getString(id + URIGEN_SERVER_KEY,
					"DEFAULT"));
			pref.setState(PrefState.CLEAN);
			prefsMap.put(id, pref);
		}

	}

	void setPrefsManager(PrefsManager prefsManager) {
		this.prefsManager = prefsManager;
	}

	boolean getMenuBooleanPreference(String name) {
		Preferences p = getPreferences(URIGEN_PREFS_SET_MENU);
		return p.getBoolean(name, false);
	}

	void setMenuBooleanPreference(String name, boolean value) {
		Preferences p = getPreferences(URIGEN_PREFS_SET_MENU);
		p.putBoolean(name, value);

	}

	String getMenuPreference(String name) {
		Preferences p = getPreferences(URIGEN_PREFS_SET_MENU);
		return p.getString(name, null);
	}

	void setMenuPreference(String name, String value) {
		Preferences p = getPreferences(URIGEN_PREFS_SET_MENU);
		p.putString(name, value);

	}

}
