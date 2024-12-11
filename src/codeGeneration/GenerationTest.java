package codeGeneration;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import org.junit.Test;
import prettyPrinter.PrettyPrinter;

public class GenerationTest {
  @Test
  public void testBalise() {
    // 1. Analyse du XML
    XMLAnalyser analyser = new XMLAnalyser();
    Model model = analyser.getModelFromString(
      "<root model=\"1\">" +
        "    <Model name=\"Balise\" id=\"1\"/>" +
        "    <Entity name=\"Balise\" id=\"2\" model=\"1\"/>" +
        "</root>"
    );

    // 2. Vérifions d'abord avec le PrettyPrinter
    PrettyPrinter printer = new PrettyPrinter();
    model.accept(printer);
    System.out.println("Structure du modèle :");
    System.out.println(printer.result());

    // 3. Générons le code Java
    JavaGenerator generator = new JavaGenerator();
    model.accept(generator);
    System.out.println("\nCode Java généré :");
    System.out.println(generator.getGeneratedCode());
  }
}