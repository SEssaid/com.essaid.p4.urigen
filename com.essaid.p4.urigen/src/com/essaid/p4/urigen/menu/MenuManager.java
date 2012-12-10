package com.essaid.p4.urigen.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.protege.editor.core.editorkit.EditorKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essaid.p4.urigen.menu.action.EntityFactory;

public class MenuManager {

	private static Logger log = LoggerFactory.getLogger(MenuManager.class);

	private static MenuManager instance;

	private Map<EditorKit, EntityFactory> kitToEntityFactory = new HashMap<EditorKit, EntityFactory>();
	private Map<EditorKit, TopMenu> kitToMenu = new HashMap<EditorKit, TopMenu>();

	private MenuManager() {

	}

	public static MenuManager INSTANCE() {
		if (instance == null) {
			instance = new MenuManager();

		}
		return instance;
	}

	public EntityFactory getEntityFactory(EditorKit kit) {
		EntityFactory factory = kitToEntityFactory.get(kit);
		if (factory == null) {
			factory = new EntityFactory( kit);
			kitToEntityFactory.put(kit, factory);
		}
		return factory;
	}

	public Set<EntityFactory> getEntityFactories() {
		return new HashSet<EntityFactory>(kitToEntityFactory.values());
	}

	public void addTopMenu(EditorKit kit, TopMenu menu) {
		if (kitToMenu.put(kit, menu) != null) {
			log.error("TopMenu already assigned to kit {}", kit);
		}

	}

	public TopMenu getTopMenu(EditorKit kit) {
		TopMenu menu = kitToMenu.get(kit);
		if (menu == null) {
			menu = new TopMenu(kit);
			kitToMenu.put(kit, menu);
		}
		return menu;
	}

	public void clearKitMappings(EditorKit kit) {
		kitToEntityFactory.remove(kit);
		kitToMenu.remove(kit);
	}

}
