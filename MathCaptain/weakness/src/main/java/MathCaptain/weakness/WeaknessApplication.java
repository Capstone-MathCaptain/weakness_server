package MathCaptain.weakness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = "MathCaptain")
@EnableTransactionManagement
public class WeaknessApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeaknessApplication.class, args);
	}

}
