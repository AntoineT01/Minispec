package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import XMLIO.XMLAnalyser;
import codeGeneration.JavaGenerator;
import metaModel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class AssociationUnidirectionnelleTest {

  private final String GENERATED_PATH = "src/generated/java/";

  @Before
  public void setup() {
    // Crée le répertoire de sortie s'il n'existe pas
    try {
      Files.createDirectories(Paths.get(GENERATED_PATH));
    } catch (IOException e) {
      fail("Impossible de créer le répertoire de test: " + e.getMessage());
    }
  }

  @Test
  public void testAssociationSimple() {
    // Chemin relatif vers le fichier de ressources
    String xmlPath = "src/test/ressources/exemple_Associations_Unidirectionnelles.xml";
    File xmlFile = new File(xmlPath);
    assertTrue("Le fichier XML devrait exister à " + xmlFile.getAbsolutePath(), xmlFile.exists());

    // Analyse du fichier XML
    XMLAnalyser analyser = new XMLAnalyser();
    Model model = analyser.getModelFromFile(xmlFile);
    assertNotNull("Le modèle ne devrait pas être null", model);

    // Vérification du modèle
    assertEquals("Le modèle devrait contenir 2 entités", 2, model.getEntities().size());

    // Génération des fichiers Java
    JavaGenerator generator = new JavaGenerator();
    model.accept(generator);

    // Vérification de la création des fichiers
    File satelliteFile = new File(GENERATED_PATH + "Satellite.java");
    File flotteFile = new File(GENERATED_PATH + "Flotte.java");

    assertTrue("Le fichier Satellite.java devrait être créé", satelliteFile.exists());
    assertTrue("Le fichier Flotte.java devrait être créé", flotteFile.exists());

    // Vérification du contenu des fichiers
    try {
      String satelliteContent = Files.readString(satelliteFile.toPath());
      String flotteContent = Files.readString(flotteFile.toPath());

      // Vérifie le package
      assertTrue("Satellite.java devrait avoir le bon package",
                 satelliteContent.contains("package generated.java"));
      assertTrue("Flotte.java devrait avoir le bon package",
                 flotteContent.contains("package generated.java"));

      // Vérifie les classes
      assertTrue("Satellite.java devrait contenir la définition de la classe",
                 satelliteContent.contains("public class Satellite"));
      assertTrue("Flotte.java devrait contenir la définition de la classe",
                 flotteContent.contains("public class Flotte"));

      // Vérifie les attributs de Satellite
      assertTrue("Satellite devrait avoir l'attribut nom",
                 satelliteContent.contains("private String nom;"));
      assertTrue("Satellite devrait avoir l'attribut id",
                 satelliteContent.contains("private Integer id;"));
      assertTrue("Satellite devrait avoir l'attribut parent",
                 satelliteContent.contains("private Flotte parent;"));

    } catch (IOException e) {
      fail("Erreur lors de la lecture des fichiers générés: " + e.getMessage());
    }
  }
}