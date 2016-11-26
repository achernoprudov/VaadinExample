package ru.list.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.list.core.data.UserRepository;
import ru.list.core.data.model.User;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.list.core", "ru.list.ui"})
@EntityScan(basePackages = "ru.list.core.data.model")
@EnableJpaRepositories(basePackages = "ru.list.core.data")
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner loadData(UserRepository repository) {
		return (args) -> {
			fillDatabase(repository);
			printData(repository);
		};
	}

	private void printData(UserRepository repository) {
		log.info("-------------------------------");
		repository.findAll().stream()
				.map(User::toString)
				.forEach(log::info);
		log.info("-------------------------------");
	}

	private void fillDatabase(UserRepository repository) {
		repository.save(new User("Mike", "White"));
		repository.save(new User("Ivan", "Popov"));
		repository.save(new User("Vladimir", "Sidorov"));
		repository.save(new User("Victor", "Ivanov"));
		repository.save(new User("Alexey", "Smirnov"));
	}

}