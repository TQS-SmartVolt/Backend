package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.tqs.smartvolt.smartvolt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be defined here if needed
    // For example, find users by username, email, etc. 
    
}
