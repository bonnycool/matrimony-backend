git push -u origin feature/backend-working
package de.germanmatrimony.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BackendApplication {

	static void main(String[] args) {
		SpringApplication.run(BackendApplication, args)
	}

}
