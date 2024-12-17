package prettyPrinter;

import metaModel.*;

public class PrettyPrinter extends Visitor {
	private StringBuilder result = new StringBuilder();
	private int indentLevel = 0;

	private void indent() {
		for (int i = 0; i < indentLevel; i++) {
			result.append("    ");
		}
	}

	public String result() {
		return result.toString();
	}

	@Override
	public void visitModel(Model e) {
		result.append("model ;\n\n");
		indentLevel++;

		for (Entity entity : e.getEntities()) {
			entity.accept(this);
			result.append("\n");
		}

		indentLevel--;
		result.append("end model\n");
	}

	@Override
	public void visitEntity(Entity e) {
		indent();
		result.append("entity ").append(e.getName()).append(";\n");
		indentLevel++;

		for (Attribute attr : e.getAttributes()) {
			attr.accept(this);
		}

		indentLevel--;
		indent();
		result.append("end entity;\n");
	}

	@Override
	public void visitAttribute(Attribute a) {
		indent();
		result.append(a.getName()).append(" : ").append(a.getType().getName()).append(";\n");
	}
}