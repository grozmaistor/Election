package grozdan.test.election;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElectionServiceApplication {
    public static void main(String[] args) {
        System.out.println("Hello and welcome to Election!");
        SpringApplication.run(ElectionServiceApplication.class, args);
    }
}
