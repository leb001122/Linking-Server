package com.linking.user.persistence;

import com.linking.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findUserByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    Optional<User> findUserByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM User u WHERE u.email LIKE CONCAT('%', :partOfEmail,'%@%')")
    List<User> findUsersByPartOfEmail(@Param("partOfEmail") String partOfEmail);

}
