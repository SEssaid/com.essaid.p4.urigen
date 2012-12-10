package com.essaid.p4.urigen.prefs;

public class Pref {
	private PrefState state = PrefState.NEW;
	private int id = -1;
	private boolean active = false;
	private String menuName = "";
	private String serverUrl = "";
	private String serverKey = "";
	private String ontologyIri = "";
	private String comment = "";
	private PrefsManager manager;

	Pref(PrefsManager prefsManager) {
		this.manager = prefsManager;
	}

	public int getId() {
		checkState(true);
		return id;
	}

	public String getMenuName() {
		checkState(true);
		return menuName;

	}

	public String getServerUrl() {
		checkState(true);
		return serverUrl;
	}

	public String getServerKey() {
		checkState(true);
		return serverKey;
	}

	public void setId(int id) {
		checkState(this.id != id);
		this.id = id;
	}

	public void setMenuName(String name) {
		checkState(this.menuName != name);
		this.menuName = name;
	}

	public void setServerUrl(String serverUrl) {
		checkState(this.serverUrl != serverUrl);
		this.serverUrl = serverUrl;
	}

	public void setServerKey(String serverKey) {
		checkState(this.serverKey != serverKey);
		this.serverKey = serverKey;
	}

	public String getOntologyIri() {
		checkState(true);
		return ontologyIri;
	}

	public void setOntologyIri(String ontologyIri) {
		checkState(this.ontologyIri != ontologyIri);
		this.ontologyIri = ontologyIri;
	}

	public String getComment() {
		checkState(true);
		return comment;
	}

	public void setComment(String comment) {
		checkState(this.comment != comment);
		this.comment = comment;
	}

	public boolean isActive() {
		checkState(true);
		return active;
	}

	public void setActive(boolean active) {
		checkState(this.active != active);
		this.active = active;
	}

	public PrefState getState() {
		checkState(true);
		return state;
	}

	void setState(PrefState state) {
		// changing state does not make it dirty
		checkState(true);
		this.state = state;
	}

	private void checkState(boolean changed) {
		if (state == PrefState.DELETED) {
			throw new RuntimeException(
					"Preference object being used after being deleted.");
		}
		// if the values are changed and the state is not new, mark dirty
		// new stays new until it is saved, i.e. new = not persisted.
		if (changed && state != PrefState.NEW) {
			state = PrefState.DIRTY;
		}
	}

	public void save() throws PrefException {
		manager.save(this);
	}

	public void delete() throws PrefException {
		manager.delete(this);
	}

	@Override
	public String toString() {

		return menuName;
	}

	public String toStringFull() {
		return "id:" + id + ",menu_name:" + menuName + ",iri:" + ontologyIri
				+ ",url:" + serverUrl + ",key:" + serverKey;
	}
}
