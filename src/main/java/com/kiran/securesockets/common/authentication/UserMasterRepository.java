package com.kiran.securesockets.common.authentication;

import com.kiran.securesockets.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMasterRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where u.active=1 and u.userName=?1 ")
	User findValidUser(String userName);

	long countByEmailIgnoreCase(String email);

	long countByEmailEqualsIgnoreCaseAndEmailNotIgnoreCase(String newEmail, String email);

	User findByUserId(Long userId);

}
