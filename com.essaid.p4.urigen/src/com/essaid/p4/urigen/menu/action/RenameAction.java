package com.essaid.p4.urigen.menu.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;

public class RenameAction extends ProtegeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RenameAction.class);

	@Override
	public void initialise() throws Exception {
		log.debug("Active action initialized.");

	}

	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		OWLEditorKit kit = (OWLEditorKit) getEditorKit();
		OWLSelectionModel sm = kit.getWorkspace().getOWLSelectionModel();
		OWLEntity entity = sm.getSelectedEntity();
		if (entity == null)
			return;

		OWLOntology ontology = kit.getModelManager().getActiveOntology();
		Set<OWLOntology> ontologies = ontology.getImportsClosure();

		List<String> names = new ArrayList<String>();
		for (OWLOntology o : ontologies) {
			for (OWLAnnotation a : entity.getAnnotations(o)) {
				OWLAnnotationProperty ap = a.getProperty();
				IRI rdfslabel = OWLRDFVocabulary.RDFS_LABEL.getIRI();
				if (ap.getIRI().toString().equals(rdfslabel.toString())) {
					names.add(((OWLLiteral) a.getValue()).getLiteral());
				}
			}
		}
		OWLEntityCreationSet<? extends OWLEntity> cs = null;
		EntityFactory ef = MenuManager.INSTANCE().getEntityFactory(
				getEditorKit());
		try {
			cs = ef.createOWLEntity(entity.getClass(), names.toString(),
					ontology.getOntologyID().getOntologyIRI());
		} catch (OWLEntityCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		IRI newIri = cs.getOWLEntity().getIRI();

		OWLEntityRenamer renamer = new OWLEntityRenamer(kit
				.getOWLModelManager().getOWLOntologyManager(), ontologies);

		List<OWLOntologyChange> changes = renamer.changeIRI(entity.getIRI(),
				newIri);
		ontology.getOWLOntologyManager().applyChanges(changes);

		Set<OWLEntity> entities = ontology.getEntitiesInSignature(newIri, true);
		OWLEntity newEntity = null;
		for (OWLEntity newE : entities) {
			if (newE.getEntityType().equals(entity.getEntityType())) {
				newEntity = newE;
			}
		}
		if (newEntity != null) {
			sm.setSelectedEntity(newEntity);
		}

	}
}
