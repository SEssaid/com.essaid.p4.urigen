package com.essaid.p4.urigen.prefs.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.prefs.Pref;
import com.essaid.p4.urigen.prefs.PrefException;
import com.essaid.p4.urigen.prefs.PrefsListener;
import com.essaid.p4.urigen.prefs.PrefsManager;

public class PrefsPresenter implements IPrefsPresenter {

	private static Logger log = LoggerFactory.getLogger(PrefsPresenter.class);

	private IPrefsView view;
	private PrefsManager prefsManager;
	private PrefsListener prefsListener;

	public PrefsPresenter(IPrefsView view) {
		this.prefsManager = PrefsManager.INSTANCE();
		this.view = view;
	}

	@Override
	public void viewRemovePrefEvent() {
		Pref p = view.presenterGetSelectedPref();
		try {
			p.delete();
		} catch (PrefException e) {
			log.error("Exception deleting Pref {}", p);
			e.printStackTrace();
		}

	}

	@Override
	public void viewInitialized() {
		this.prefsListener = new PrefsListener() {

			@Override
			public void prefRemoved(Pref pref) {
				view.presenterRemovePref(pref);
			}

			@Override
			public void prefChanged(Pref pref) {
				view.presenterPrefChanged(pref);
			}

			@Override
			public void prefAdded(Pref pref) {
				view.presenterAddPref(pref);

			}
		};
		prefsManager.addPrefsListener(prefsListener);
		view.presenterListPrefs(prefsManager.getSortedAllPrefs());
	}

	@Override
	public void viewDestroyed() {
		prefsManager.removePrefsListener(prefsListener);

	}

	@Override
	public void viewPrefDoubleClickEvent() {
		Pref pref = view.presenterGetSelectedPref();
		pref = view.presenterEditPref(pref);
		if (pref != null) {
			try {
				pref.save();
			} catch (PrefException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			view.presenterPrefChanged(pref);
		}

	}

	@Override
	public void viewPrefChanged(Pref pref) {
		try {
			pref.save();
		} catch (PrefException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void viewAddPrefEvent() {
		Pref newPref = prefsManager.createNewPref();
		newPref = view.presenterEditPref(newPref);
		if (newPref != null) {
			try {
				newPref.save();
			} catch (PrefException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
