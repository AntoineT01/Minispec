package metaModel;

public class EntityType extends Type {
  private Entity referencedEntity;

  public Entity getReferencedEntity() {
    return referencedEntity;
  }

  public void setReferencedEntity(Entity entity) {
    this.referencedEntity = entity;
  }

  @Override
  public void accept(Visitor v) {
    v.visitEntityType(this);
  }
}