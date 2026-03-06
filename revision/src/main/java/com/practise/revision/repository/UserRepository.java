package com.practise.revision.repository;


import com.practise.revision.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {

             Optional<User> findById(Long  id);
            public User findByEmail(String email);
            public List<User> findAll();
}
