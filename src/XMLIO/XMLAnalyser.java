package XMLIO;

import javax.xml.parsers.*;

import metaModel.Entity;
import org.w3c.dom.*;
import org.xml.sax.*;
import metaModel.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {
	protected Map<String, MinispecElement> minispecIndex;
	protected Map<String, Element> xmlElementIndex;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<>();
		this.xmlElementIndex = new HashMap<>();
	}

	protected void secondRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				minispecElementFromXmlElement((Element)n);
			}
		}
	}

	public Model getModelFromString(String contents) {
		try {
			// Création d'une fabrique de documents
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			// Création d'un constructeur de documents
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Création d'un flux d'entrée à partir de la chaîne
			InputStream stream = new ByteArrayInputStream(contents.getBytes("UTF-8"));

			// Parse le document XML
			Document document = builder.parse(stream);

			return getModelFromDocument(document);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Model getModelFromFilenamed(String filename) {
		try {
			// Chercher d'abord dans le dossier resources
			String resourcePath = "src/test/resources/" + filename;
			File file = new File(resourcePath);

			// Si le fichier n'existe pas dans resources, essayer le chemin direct
			if (!file.exists()) {
				file = new File(filename);
			}

			if (!file.exists()) {
				throw new FileNotFoundException("Le fichier " + filename + " n'a pas été trouvé");
			}

			return getModelFromFile(file);
		} catch (FileNotFoundException e) {
			System.err.println("Erreur lors de la lecture du fichier: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	protected Model modelFromElement(Element e) {
		Model model = new Model();
		if (e.hasAttribute("name")) {
			model.setName(e.getAttribute("name"));
		}
		System.out.println("Création du modèle avec ID: " + e.getAttribute("id"));
		return model;
	}

	protected Entity entityFromElement(Element e) {
		String name = e.getAttribute("name");
		Entity entity = new Entity();
		entity.setName(name);

		System.out.println("Création de l'entité: " + name);

		// Traitement des attributs
		NodeList attributes = e.getElementsByTagName("attribute");
		for (int i = 0; i < attributes.getLength(); i++) {
			Element attrElement = (Element) attributes.item(i);
			Attribute attribute = attributeFromElement(attrElement);
			entity.addAttribute(attribute);
		}

		// Récupération du modèle parent
		String modelId = e.getAttribute("model");
		System.out.println("Recherche du modèle avec ID: " + modelId);
		Element modelElement = xmlElementIndex.get(modelId);

		if (modelElement != null) {
			System.out.println("Modèle trouvé dans xmlElementIndex");
			Model model = (Model) minispecElementFromXmlElement(modelElement);
			System.out.println("Ajout de l'entité " + name + " au modèle");
			model.addEntity(entity);
		} else {
			System.out.println("ERREUR: Modèle non trouvé pour ID: " + modelId);
		}

		return entity;
	}

	protected Attribute attributeFromElement(Element e) {
		Attribute attribute = new Attribute();
		attribute.setName(e.getAttribute("name"));

		String typeName = e.getAttribute("type");
		Type type;

		if (typeName.equals("String") || typeName.equals("Integer")) {
			type = new PrimitiveType();
			type.setName(typeName);
		} else {
			EntityType entityType = new EntityType();
			entityType.setName(typeName);
			type = entityType;
		}

		attribute.setType(type);
		return attribute;
	}

	protected MinispecElement minispecElementFromXmlElement(Element e) {
		String id = e.getAttribute("id");
		MinispecElement result = this.minispecIndex.get(id);

		if (result != null) {
			return result;
		}

		String tag = e.getTagName().toLowerCase();
		if (tag.equals("model")) {
			result = modelFromElement(e);
		} else if (tag.equals("entity")) {
			result = entityFromElement(e);
		}

		if (result != null) {
			this.minispecIndex.put(id, result);
		}

		return result;
	}

	protected void processAllElements(Element root) {
		// Traite l'élément courant
		if (root.hasAttribute("id")) {
			String id = root.getAttribute("id");
			if (root.getTagName().toLowerCase().equals("entity")) {
				System.out.println("Processing entity with ID: " + id);
				minispecElementFromXmlElement(root);
			}
		}

		// Traite les enfants récursivement
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				processAllElements((Element) node);
			}
		}
	}

	protected void firstRound(Element el) {
		if (el.hasAttribute("id")) {
			String id = el.getAttribute("id");
			xmlElementIndex.put(id, el);
			System.out.println("Indexation de l'élément " + el.getTagName() + " avec ID: " + id);
		}

		NodeList children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				firstRound((Element) child);
			}
		}
	}

	public Model getModelFromDocument(Document document) {
		System.out.println("Début de l'analyse du document");
		Element rootElement = document.getDocumentElement();

		// Première passe : indexer tous les éléments
		firstRound(rootElement);

		String modelId = rootElement.getAttribute("model");
		System.out.println("ID du modèle racine: " + modelId);

		Element modelElement = xmlElementIndex.get(modelId);
		if (modelElement == null) {
			throw new RuntimeException("Model element not found with id: " + modelId);
		}

		// Créer le modèle
		Model model = (Model) minispecElementFromXmlElement(modelElement);

		// Traiter tous les éléments du document
		System.out.println("Traitement de tous les éléments...");
		processAllElements(rootElement);

		System.out.println("Nombre final d'entités dans le modèle: " + model.getEntities().size());
		return model;
	}

	public Model getModelFromFile(File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			return getModelFromDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}