package metaModel;

public class PrimitiveType extends Type {
  @Override
  public void accept(Visitor v) {
    v.visitPrimitiveType(this);
  }
}