package net.paulgray.exampleltiapp;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.math.BigDecimal;

/**
 * Created by paul on 3/23/16.
 */
@Configuration
@ComponentScan//("net.paulgray.mocklti2.*")
@ImportResource("spring/applicationContext.xml")
@EnableAutoConfiguration
@SpringBootApplication
public class ExampleLtiApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExampleLtiApp.class, args);
    }

}
