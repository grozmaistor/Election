package grozdan.test.election;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElectionServiceApplication {
    public static void main(String[] args) {
        System.out.println("Hello and welcome to  * E L E C T I O N *  !");
        SpringApplication.run(ElectionServiceApplication.class, args);
    }
}
