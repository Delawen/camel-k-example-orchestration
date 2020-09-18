// camel-k: language=java

import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class AutoPerson extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    // Preparing properties to build a GeoJSON Feature
    Processor processor = new Processor() {

      String[] firstname = new String[] { "Aleja", "Almerinda", "Ambrosia", "Benilda", "Benilde", "Bercia", 
      "Cayetana", "Ermisinda", "Escolástica", "Esmaragda", "Esmerencia"};
      String[] lastname = new String[] { "", "Zuzunaga", "Sorní", "Sandemetrio", "Bonachera", "Sazón", "Piesplanos", "Parraverde",
          "Alcoholado" };

      Random r = new Random();

      @Override
      public void process(Exchange exchange) throws Exception {
        StringBuilder name = new StringBuilder();
        name.append(firstname[r.nextInt(firstname.length)]);
        name.append(" ");
        name.append(lastname[r.nextInt(lastname.length)]);
         exchange.getMessage().setBody(name.toString());
      }

    };

    // Listen to kafka cat broker
    from("timer:java?period=10s")
      // Someone arrived today with a random name
      .process(processor)
      .log("${body} wants to adopt a cat.")

      // Send it to Kafka cat broker
      .to("kafka:person?brokers=my-cluster-kafka-bootstrap:9092")

      // Write some log to know it finishes properly
      .log("Will they find a family?");

  }
}
