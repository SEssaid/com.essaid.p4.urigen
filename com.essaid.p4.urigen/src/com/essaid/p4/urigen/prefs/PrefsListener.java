package com.essaid.p4.urigen.prefs;

public interface PrefsListener {

	public void prefAdded(Pref pref);

	public void prefRemoved(Pref pref);

	public void prefChanged(Pref pref);
}
