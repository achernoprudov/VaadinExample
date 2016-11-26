package ru.list.core.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.list.core.data.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	 List<User> findByLastNameStartsWithIgnoreCase(String lastName);
}
