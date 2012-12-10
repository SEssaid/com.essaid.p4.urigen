package com.essaid.p4.urigen.prefs.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.essaid.p4.urigen.prefs.Pref;

public class PrefsTableModel extends AbstractTableModel {

	List<Pref> prefs = new ArrayList<Pref>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PrefsTableModel() {
	}

	@Override
	public int getRowCount() {
		return prefs.size();
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return prefs.get(rowIndex).isActive();
		case 1:
			return prefs.get(rowIndex).getMenuName();
		case 2:
			return prefs.get(rowIndex).getOntologyIri();
		case 3:
			return prefs.get(rowIndex).getServerUrl();
		case 4:
			return prefs.get(rowIndex).getServerKey();
		case 5:
			return prefs.get(rowIndex).getComment();
		case 6:
			return prefs.get(rowIndex).getId();
		default:
			return "undefined";
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			prefs.get(rowIndex).setActive((Boolean) aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	public void addPref(Pref pref) {
		int pos = prefs.size();
		prefs.add(pref);
		fireTableRowsInserted(pos, pos);
	}

	public void removePref(Pref pref) {
		int pos = prefs.indexOf(pref);
		prefs.remove(pref);
		fireTableRowsDeleted(pos, pos);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	public List<Pref> getPrefs() {
		return prefs;
	}
}
