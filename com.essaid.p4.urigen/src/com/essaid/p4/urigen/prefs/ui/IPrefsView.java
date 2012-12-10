package com.essaid.p4.urigen.prefs.ui;

import java.util.List;

import com.essaid.p4.urigen.prefs.Pref;

public interface IPrefsView {

	void presenterAddPref(Pref pref);

	void presenterListPrefs(List<Pref> prefs);

	void presenterRemovePref(Pref pref);
	
	Pref presenterGetSelectedPref();
	
	Pref presenterEditPref(Pref pref);
	
	void presenterPrefChanged(Pref pref);
}
