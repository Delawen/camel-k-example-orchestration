// camel-k: language=java 

import org.apache.camel.builder.RouteBuilder;

public class PersonInput extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    //Listen to kafka person broker
    from("kafka:person?brokers=my-cluster-kafka-bootstrap:9092")
    .log("Message received from Kafka : ${body}")
    .log("${body} wants to adopt a cat")

    //Store the name of the person
    .setProperty("person", simple("${body}"))

    //Search for a lonely cat
    .log("...looking for available cats...")
    .setBody().simple("SELECT id, name, image FROM cat WHERE person is NULL LIMIT 1;")
    .to("jdbc:postgresBean?")

    .choice()
      .when(header("CamelJdbcRowCount").isGreaterThanOrEqualTo(1))
        .setProperty("catname", simple("${body[0][name]}"))
        .setProperty("catimage", simple("${body[0][image]}"))
        .setProperty("catid", simple("${body[0][id]}"))
        .log("Cat found called ${exchangeProperty.catname} with ID ${exchangeProperty.catid}")
        //There's a cat available, adopt it!
        .setBody().simple("UPDATE cat SET person='${exchangeProperty.person}' WHERE id=${exchangeProperty.catid}")
        .to("jdbc:postgresBean?")
            
        //Write some log to know it finishes properly
        .setBody().simple("Congratulations! ${exchangeProperty.catname} adopted ${exchangeProperty.person}. See how happy is on ${exchangeProperty.catimage}.")
        .to("log:info")
      .otherwise()
        //Write some log to know it finishes properly
        .setBody().simple("We are sorry, there's no cat looking for a family at this moment.")
        .to("log:info")
    .end();
  }

}
