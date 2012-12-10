package com.essaid.p4.urigen.prefs.ui;

import com.essaid.p4.urigen.prefs.Pref;

public interface IPrefsPresenter {

	void viewInitialized();

	void viewAddPrefEvent();

	void viewRemovePrefEvent();

	void viewPrefDoubleClickEvent();
	
	void viewPrefChanged(Pref pref);
	
	void viewDestroyed();

}
