// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import model.Cat;

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
}
