package com.essaid.p4.urigen.menu.action;

import java.net.URISyntaxException;
import java.util.UUID;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.AutoIDException;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.MenuManager;
import com.essaid.p4.urigen.menu.TopMenu;
import com.essaid.p4.urigen.prefs.Pref;
import com.essaid.p4.urigen.prefs.PrefsManager;

import uk.ac.ebi.fgpt.urigen.entity.ServerURLConnection;
import uk.ac.ebi.fgpt.urigen.web.view.PreferenceBean;
import uk.ac.ebi.fgpt.urigen.web.view.UrigenEntityBean;
import uk.ac.ebi.fgpt.urigen.web.view.UrigenRequestBean;

public class EntityFactory extends CustomOWLEntityFactory {

	private static Logger log = LoggerFactory.getLogger(EntityFactory.class);

	EntityNameInfo nameInfo;

	private int remoteUserId = -1;
	private int remotePrefId = -1;

	private boolean previewing;

	private String ontologyIri;
	private String serverKey;
	private String serverUrl;

	private boolean needsUpdate = true;
	private boolean isValid = false;

	private OWLEditorKit kit;

	public EntityFactory(EditorKit kit) {
		super((OWLModelManager) kit.getModelManager());
		this.kit = (OWLEditorKit) kit;
	}

	@Override
	public <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type,
			String shortName, IRI base) throws OWLEntityCreationException {
		this.previewing = true;
		OWLEntityCreationSet<T> set = super.preview(type, shortName, base);
		this.previewing = false;

		return set;
	}

	@Override
	protected <T extends OWLEntity> EntityNameInfo generateName(Class<T> type,
			String shortName, IRI baseURI) throws AutoIDException,
			URISyntaxException, OWLEntityCreationException {
		if (needsUpdate) {
			update();
		}
		if (previewing) {
			UUID uuid = UUID.randomUUID();
			nameInfo = new EntityNameInfo(IRI.create(kit.getOWLModelManager()
					.getActiveOntology().getOntologyID().getOntologyIRI()
					.toString()
					+ "/" + "urigen2/" + uuid), "" + uuid, shortName);
			previewing = false;
			return nameInfo;
		} else {

			if (isValid) {
				try {
					ServerURLConnection con = new ServerURLConnection(serverUrl);
					UrigenRequestBean request = new UrigenRequestBean();
					request.setLabel(shortName);
					request.setPreferencesId(remotePrefId);
					request.setUserId(remoteUserId);
					request.setComment("URiGEN_2");
					log.debug("Sending request {}", request);
					UrigenEntityBean response = con.getNewUri(request,
							serverKey);
					// log.debug("Returned iri: {}",
					// response.getGeneratedUri());

					if (!response.getStatusOK()) {
						log.debug("Status not OK from server {}", response);
						return nameInfo;
					}

					return new EntityNameInfo(IRI.create(response
							.getGeneratedUri()), "" + response.getId(),
							shortName);
				} catch (Exception e) {
					log.debug("Exception getting uri from server {}", e);
					return nameInfo;
				}
			} else {
				if (nameInfo != null) {
					return nameInfo;
				} else {
					// this happens when doing a direct rename without a preview
					UUID uuid = UUID.randomUUID();
					nameInfo = new EntityNameInfo(IRI.create(kit
							.getOWLModelManager().getActiveOntology()
							.getOntologyID().getOntologyIRI().toString()
							+ "/" + "urigen2/" + uuid), "" + uuid, shortName);
					return nameInfo;
				}
			}
		}
	}

	private void update() {
		boolean valid = false;
		TopMenu top = MenuManager.INSTANCE().getTopMenu(kit);
		if (top.getFollowAction().isFollow()) {
			// get the following settings
			serverUrl = top.getServerUrl();
			ontologyIri = top.getOntologyIri();
			if (serverUrl != null && ontologyIri != null) {
				for (Pref pref : PrefsManager.INSTANCE().getSortedAllPrefs()) {
					if (pref.getServerUrl().equals(serverUrl)
							&& pref.getOntologyIri().equals(ontologyIri)) {
						ontologyIri = pref.getOntologyIri();
						serverKey = pref.getServerKey();
						serverUrl = pref.getServerUrl();
						if (!ontologyIri.trim().isEmpty()
								&& !serverKey.trim().isEmpty()
								&& !serverUrl.trim().isEmpty()) {
							valid = true;
						}
					}
				}
			}
		} else {
			// get the selected settings
			Pref pref = top.getGeneratorAction().getSelectedPref();
			if (pref != null) {
				ontologyIri = pref.getOntologyIri();
				serverKey = pref.getServerKey();
				serverUrl = pref.getServerUrl();
				if (!ontologyIri.trim().isEmpty()
						&& !serverKey.trim().isEmpty()
						&& !serverUrl.trim().isEmpty()) {
					valid = true;
				}
			}
		}
		if (valid) {
			try {
				ServerURLConnection con = new ServerURLConnection(serverUrl);
				PreferenceBean pbean = con.getUrigenPreference(IRI
						.create(ontologyIri));
				remotePrefId = pbean.getPreferenceId();
				remoteUserId = con.getUrigenUserByApiKey(serverKey).getId();
			} catch (Exception e) {
				log.error(
						"Error resolving preference and user ids from remote server {}",
						e);
				valid = false;
			}
		}
		isValid = valid;
		needsUpdate = false;

	}

	public void setNeedsUpdate(boolean update) {
		this.needsUpdate = update;
	}

}
