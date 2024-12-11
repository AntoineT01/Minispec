package codeGeneration;

import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class JavaGenerator extends Visitor {

  private StringBuilder code = new StringBuilder();

  public String getGeneratedCode() {
    return code.toString();
  }

  @Override
  public void visitModel(Model model) {
    // En-tête du fichier Java
    code.append("package generated;\n\n");
    code.append("/**\n");
    code.append(" * Classe générée automatiquement pour le modèle ");
    code.append(" */\n");

    // Visitons les entités du modèle
    for (Entity entity : model.getEntities()) {
      entity.accept(this);
    }
  }

  @Override
  public void visitEntity(Entity entity) {
    // Génération de la classe
    code.append("public class ").append(entity.getName()).append(" {\n");

    // Pour l'instant, juste une classe vide
    code.append("    // TODO: Ajouter les attributs et méthodes\n");

    code.append("}\n");
  }
}