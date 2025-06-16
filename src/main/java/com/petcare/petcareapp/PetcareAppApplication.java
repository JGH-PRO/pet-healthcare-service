package com.petcare.petcareapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
    info = @Info(
        title = "Pet Care API",
        version = "1.0.0",
        description = "API documentation for the Pet Care application.",
        contact = @Contact(name = "Support Team", email = "support@petcare.com"),
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
    )
)
@SpringBootApplication
public class PetcareAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetcareAppApplication.class, args);
	}

}
