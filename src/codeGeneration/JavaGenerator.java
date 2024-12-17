package codeGeneration;

import metaModel.*;
import java.io.*;
import java.nio.file.*;

public class JavaGenerator extends Visitor {
  private StringBuilder code = new StringBuilder();
  private int indentLevel = 0;
  private String outputDir = "src/generated/java/";  // Répertoire de sortie par défaut
  private String currentClassName;  // Pour suivre la classe en cours de génération

  public JavaGenerator() {
    // Crée le répertoire de sortie s'il n'existe pas
    try {
      Files.createDirectories(Paths.get(outputDir));
    } catch (IOException e) {
      System.err.println("Impossible de créer le répertoire de sortie: " + e.getMessage());
    }
  }

  public void setOutputDir(String dir) {
    this.outputDir = dir;
    if (!this.outputDir.endsWith("/")) {
      this.outputDir += "/";
    }
    try {
      Files.createDirectories(Paths.get(outputDir));
    } catch (IOException e) {
      System.err.println("Impossible de créer le répertoire de sortie: " + e.getMessage());
    }
  }

  private void writeToFile() {
    if (currentClassName != null && code.length() > 0) {
      try {
        String fileName = outputDir + currentClassName + ".java";
        System.out.println("Création du fichier: " + fileName);
        Files.write(Paths.get(fileName), code.toString().getBytes());
        // Réinitialise le buffer pour la prochaine classe
        code.setLength(0);
      } catch (IOException e) {
        System.err.println("Erreur lors de l'écriture du fichier: " + e.getMessage());
      }
    }
  }

  private void indent() {
    for (int i = 0; i < indentLevel; i++) {
      code.append("    ");
    }
  }

  @Override
  public void visitModel(Model model) {
    for (Entity entity : model.getEntities()) {
      // Réinitialise le buffer pour chaque nouvelle entité
      code.setLength(0);

      // Génère l'en-tête du fichier
      code.append("package generated;\n\n");
      code.append("import java.util.*;\n\n");

      // Génère la classe
      entity.accept(this);

      // Écrit le fichier
      writeToFile();
    }
  }

  @Override
  public void visitEntity(Entity entity) {
    currentClassName = entity.getName();
    indent();
    code.append("public class ").append(entity.getName()).append(" {\n");
    indentLevel++;

    // Attributs
    for (Attribute attr : entity.getAttributes()) {
      attr.accept(this);
    }

    // Constructeur
    code.append("\n");
    indent();
    code.append("public ").append(entity.getName()).append("() {}\n");

    // Getters et setters
    for (Attribute attr : entity.getAttributes()) {
      generateAccessors(attr);
    }

    indentLevel--;
    indent();
    code.append("}\n");
  }

  @Override
  public void visitAttribute(Attribute attr) {
    indent();
    code.append("private ").append(attr.getType().getName())
      .append(" ").append(attr.getName()).append(";\n");
  }

  private void generateAccessors(Attribute attr) {
    String capitalizedName = attr.getName().substring(0, 1).toUpperCase() +
      attr.getName().substring(1);

    // Getter
    code.append("\n");
    indent();
    code.append("public ").append(attr.getType().getName())
      .append(" get").append(capitalizedName).append("() {\n");
    indentLevel++;
    indent();
    code.append("return ").append(attr.getName()).append(";\n");
    indentLevel--;
    indent();
    code.append("}\n");

    // Setter
    code.append("\n");
    indent();
    code.append("public void set").append(capitalizedName)
      .append("(").append(attr.getType().getName()).append(" ")
      .append(attr.getName()).append(") {\n");
    indentLevel++;
    indent();
    code.append("this.").append(attr.getName()).append(" = ")
      .append(attr.getName()).append(";\n");
    indentLevel--;
    indent();
    code.append("}\n");
  }
}