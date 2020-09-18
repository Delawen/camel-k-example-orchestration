// camel-k: language=java
// camel-k: language=java property-file=orchestration.properties  dependency=camel:gson
// camel-k: source=customizers/PostgreSQLCustomizer.java 


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CatInput extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    //Listen to kafka cat broker
    from("kafka:cat?brokers=my-cluster-kafka-bootstrap:9092")
    .log("Message received from Kafka : ${body}")
    .unmarshal().json(JsonLibrary.Gson, Cat.class)

    //Store it on the database with a null person
    .setBody().simple("INSERT INTO cat (name, image) VALUES ('${body.name}', '${body.image}')")
    .to("jdbc:postgresBean?")
        
    //Write some log to know it finishes properly
    .log("Cat stored.");
      
  }

class Cat {

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

}
