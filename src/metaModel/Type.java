package metaModel;

public abstract class Type {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public abstract void accept(Visitor v);
}