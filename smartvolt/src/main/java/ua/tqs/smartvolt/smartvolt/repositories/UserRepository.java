package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
