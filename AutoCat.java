// camel-k: language=java dependency=camel:gson

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class AutoCat extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    // Preparing properties to build a GeoJSON Feature
    Processor processor = new Processor() {

      String[] title = new String[] { "", "Lady", "Princess", "Mighty", "Your Highness", "Little", "Purry", "Empress", "Doctor", "Professor" };
      String[] firstname = new String[] { "Dewey", "Butter", "Merlin", "Epiphany", "Blasfemy", "Metaphor", "Fuzzy",
          "Whity", "Astro", "Salty", "Smol", "Whiskers", "Scully" };
      String[] lastname = new String[] { "", "Luna", "Wild", "Dragonis", "Firefly", "Puff", "Purrcy", "Priss",
          "Catsie" };

      Random r = new Random();

      @Override
      public void process(Exchange exchange) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("image", exchange.getProperty("catimage").toString());

        StringBuilder name = new StringBuilder();
        name.append(title[r.nextInt(title.length)]);
        name.append(" ");
        name.append(firstname[r.nextInt(firstname.length)]);
        name.append(" ");
        name.append(lastname[r.nextInt(lastname.length)]);

        exchange.setProperty("catname", name.toString());
        map.put("name", name.toString().trim());

        exchange.getMessage().setBody(map);

      }

    };

    // Listen to kafka cat broker
    from("timer:java?period=10s")
      
      // Take a random image
      .to("https://api.thecatapi.com/v1/images/search")
      .unmarshal().json(JsonLibrary.Gson)
      .log("A new cat arrived today ${body[0][url]}")
      .setProperty("catimage", simple("${body[0][url]}"))

      // name cat and prepare json
      .process(processor)
      .log("${body}")
      .marshal().json(JsonLibrary.Gson)
      .log("We named them ${exchangeProperty.catname}")

      // Send it to Kafka cat broker
      .to("kafka:cat?brokers=my-cluster-kafka-bootstrap:9092")

      // Write some log to know it finishes properly
      .log("Cat is looking for a family.");

  }
}
