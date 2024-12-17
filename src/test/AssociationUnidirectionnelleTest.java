package test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import XMLIO.XMLAnalyser;
import codeGeneration.JavaGenerator;
import metaModel.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class AssociationUnidirectionnelleTest {

  private final String TEST_OUTPUT_DIR = "src/generated/java/";

  @Before
  public void setup() {
    // Crée le répertoire de sortie s'il n'existe pas
    try {
      Files.createDirectories(Paths.get(TEST_OUTPUT_DIR));
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

    // Vérification du modèle comme avant...

    // Génération des fichiers Java
    JavaGenerator generator = new JavaGenerator();
    generator.setOutputDir(TEST_OUTPUT_DIR);
    model.accept(generator);

    // Vérification de la création des fichiers
    File satelliteFile = new File(TEST_OUTPUT_DIR + "Satellite.java");
    File flotteFile = new File(TEST_OUTPUT_DIR + "Flotte.java");

    assertTrue("Le fichier Satellite.java devrait être créé", satelliteFile.exists());
    assertTrue("Le fichier Flotte.java devrait être créé", flotteFile.exists());

    // Vérification optionnelle du contenu des fichiers
    try {
      String satelliteContent = Files.readString(satelliteFile.toPath());
      String flotteContent = Files.readString(flotteFile.toPath());

      assertTrue("Satellite.java devrait contenir la définition de la classe",
                 satelliteContent.contains("public class Satellite"));
      assertTrue("Flotte.java devrait contenir la définition de la classe",
                 flotteContent.contains("public class Flotte"));

    } catch (IOException e) {
      fail("Erreur lors de la lecture des fichiers générés: " + e.getMessage());
    }
  }

  @After
  public void cleanup() {
    // Optionnel : supprime les fichiers générés après le test
        /*
        try {
            Files.deleteIfExists(Paths.get(TEST_OUTPUT_DIR + "Satellite.java"));
            Files.deleteIfExists(Paths.get(TEST_OUTPUT_DIR + "Flotte.java"));
        } catch (IOException e) {
            System.err.println("Erreur lors du nettoyage: " + e.getMessage());
        }
        */
  }
}