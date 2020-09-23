package model;

public class Cat {

    private String name;
    private String image;
  
    public String name() {
        return name;
    }
  
    public void setName(String name) {
        this.name = name;
    }
  
    public String image() {
        return image;
    }
  
    public void setImage(String image) {
        this.image = image;
    }
  
    @Override
    public String toString() {
      return "Cat [image=" + image + ", name=" + name + "]";
    }
  }
  