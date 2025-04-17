package com.policene.voluntech;

import com.policene.voluntech.models.entities.User;
import com.policene.voluntech.models.enums.UserRole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VoluntechApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoluntechApplication.class, args);
	}

}
