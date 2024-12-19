package codeGeneration;

import metaModel.*;
import java.io.*;
import java.nio.file.*;

public class JavaGenerator extends Visitor {
  private StringBuilder code = new StringBuilder();
  private int indentLevel = 0;
  private String baseOutputDir = "src/";
  private String packageName = "generated.java";
  private String currentClassName;

  public JavaGenerator() {
    String packagePath = baseOutputDir + packageName.replace('.', '/');
    try {
      Files.createDirectories(Paths.get(packagePath));
    } catch (IOException e) {
      System.err.println("Impossible de créer le répertoire de sortie: " + e.getMessage());
    }
  }

  private void writeToFile() {
    if (currentClassName != null && code.length() > 0) {
      try {
        String packagePath = baseOutputDir + packageName.replace('.', '/');
        String fileName = packagePath + "/" + currentClassName + ".java";
        System.out.println("Création du fichier: " + fileName);
        Files.write(Paths.get(fileName), code.toString().getBytes());
        code.setLength(0);
      } catch (IOException e) {
        System.err.println("Erreur lors de l'écriture du fichier: " + e.getMessage());
      }
    }
  }

  private void indent() {
    code.append("    ".repeat(indentLevel));
  }

  @Override
  public void visitModel(Model model) {
    for (Entity entity : model.getEntities()) {
      // Reset le buffer pour chaque entité
      code.setLength(0);
      currentClassName = entity.getName();

      // En-tête du fichier
      code.append("package ").append(packageName).append(";\n\n");
      code.append("import java.util.*;\n\n");

      // Génère la classe
      visitEntity(entity);

      // Écrit le fichier
      writeToFile();
    }
  }

  @Override
  public void visitEntity(Entity entity) {
    // Début de la classe
    code.append("public class ").append(entity.getName()).append(" {\n");
    indentLevel++;

    // Génère les attributs
    for (Attribute attr : entity.getAttributes()) {
      indent();
      code.append("private ").append(attr.getType().getName())
        .append(" ").append(attr.getName()).append(";\n");
    }

    // Constructeur par défaut
    code.append("\n");
    indent();
    code.append("public ").append(entity.getName()).append("() {}\n");

    // Getters et setters
    for (Attribute attr : entity.getAttributes()) {
      generateAccessors(attr);
    }

    // Fin de la classe
    indentLevel--;
    code.append("}\n");
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