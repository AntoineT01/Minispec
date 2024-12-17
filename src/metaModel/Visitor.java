package metaModel;

public class Visitor {
	public void visitModel(Model e) {}
	public void visitEntity(Entity e) {}
	public void visitAttribute(Attribute a) {}
	public void visitPrimitiveType(PrimitiveType t) {}
	public void visitEntityType(EntityType t) {}
}