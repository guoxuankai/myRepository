package org.lpl.demo;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App {
		
    public static void main( String[] args ){
	
        SpringApplication.run(App.class, args);
    	
    }
}
