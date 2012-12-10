package com.essaid.p4.urigen.prefs.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;
import com.essaid.p4.urigen.menu.action.EntityFactory;
import com.essaid.p4.urigen.prefs.Pref;

public class PrefsPanel extends OWLPreferencesPanel implements IPrefsView {

	private static Logger log = LoggerFactory.getLogger(PrefsPanel.class);
	private PrefsPresenter presenter;
	private JButton jbAddPref;
	private JButton jbDeletePref;
	private JScrollPane scrollPane;

	private DefaultTableColumnModel tcm;
	private PrefsTableModel tm = new PrefsTableModel();
	private JTable jtable;
	private int selectedTableRow;
	private int selectedModelRow;
	private MouseAdapter mouseListener;
	private ListSelectionListener selectionListener;
	private TableModelListener tmListener;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void dispose() throws Exception {

	}

	@Override
	public void applyChanges() {
		for (EntityFactory ef : MenuManager.INSTANCE().getEntityFactories()) {
			ef.setNeedsUpdate(true);
		}
	}

	@Override
	public void initialise() throws Exception {
		this.presenter = new PrefsPresenter(this);
		buildGui();
		presenter.viewInitialized();
	}

	@Override
	public void presenterAddPref(Pref pref) {
		tm.addPref(pref);
	}

	@Override
	public void presenterListPrefs(List<Pref> prefs) {
		for (Pref p : prefs) {
			tm.addPref(p);
		}
	}

	@Override
	public void presenterRemovePref(Pref pref) {
		tm.removePref(pref);
	}

	@Override
	public Pref presenterGetSelectedPref() {
		return tm.getPrefs().get(selectedModelRow);

	}

	private void buildGui() {
		setLayout(new MigLayout(new LC().fill()));

		// the add button
		jbAddPref = new JButton("Add");
		jbAddPref.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.viewAddPrefEvent();
			}
		});

		add(jbAddPref);

		// the delete button
		jbDeletePref = new JButton("Delete");
		jbDeletePref.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.viewRemovePrefEvent();

			}
		});
		add(jbDeletePref, new CC().wrap());

		// the table
		initTable();
		scrollPane = new JScrollPane(jtable);
		add(scrollPane, new CC().grow().spanX());

		revalidate();

	}

	private void initTable() {
		initTableColModel();
		jtable = new JTable(tm, tcm);
		jtable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		// selection listener
		this.selectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				log.debug("Table selection listener {} called with {}",
						new Object[] { this, e });
				if (e.getValueIsAdjusting()) {
					return;
				}
				selectedTableRow = jtable.getSelectedRow();
				log.debug("Table selected row: {}", selectedTableRow);
				if (selectedTableRow > -1) {
					selectedModelRow = jtable
							.convertRowIndexToModel(selectedTableRow);
				} else {
					selectedModelRow = -1;
				}
				log.debug("Model selected row: {}", selectedModelRow);

			}
		};
		jtable.getSelectionModel().addListSelectionListener(selectionListener);

		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtable.setAutoCreateRowSorter(true);

		// model listener
		this.tmListener = new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				if (e.getColumn() == 0) {
					presenter.viewPrefChanged(tm.getPrefs()
							.get(e.getFirstRow()));
				}

			}
		};
		tm.addTableModelListener(tmListener);

		// mouse listner
		this.mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					presenter.viewPrefDoubleClickEvent();
				}
			}
		};
		jtable.addMouseListener(mouseListener);

	}

	private void initTableColModel() {
		tcm = new DefaultTableColumnModel();
		TableColumn column = null;

		column = new TableColumn(0);
		column.setHeaderValue("Active");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(1);
		column.setHeaderValue("Menu label");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(2);
		column.setHeaderValue("Server ontology IRI");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(3);
		column.setHeaderValue("Server URL");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(4);
		column.setHeaderValue("Server key");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(5);
		column.setHeaderValue("Comment");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);

		column = new TableColumn(6);
		column.setHeaderValue("id");
		column.setResizable(true);
		column.sizeWidthToFit();
		tcm.addColumn(column);
	}

	@Override
	public Pref presenterEditPref(Pref pref) {
		NewPrefPanel newPrefPanel = new NewPrefPanel();
		UIHelper helper = new UIHelper(getOWLEditorKit());
		int value = helper.showDialog("Add new preference",
				newPrefPanel.getPanel());
		if (value == JOptionPane.OK_OPTION) {
			newPrefPanel.populatePref(pref);
			return pref;
		} else {
			return null;
		}
	}

	@Override
	public void presenterPrefChanged(Pref pref) {
		int index = tm.getPrefs().indexOf(pref);
		tm.fireTableRowsUpdated(index, index);

	}
}
