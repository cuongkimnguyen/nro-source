package com.ngocrong.repository;

import com.ngocrong.data.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Integer> {

    @Query(value = "Select * from nr_user where username = :username and password = :password limit 1", nativeQuery = true)
    List<UserData> findByUsernameAndPassword(String username, String password);
       
    @Modifying
    @Query("UPDATE UserData p SET p.status = 1 WHERE p.username = :username")
    void setBanAccount(String username);
    
    @Query(value = "SELECT MAX(id) FROM nr_user", nativeQuery = true)
    Optional<Integer> findMaxId();
    
    @Query(value = "Select * from nr_user where username = :username limit 1", nativeQuery = true)
    List<UserData> findByUsername(String username);
    
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE nr_user SET username = :usernameNew, password = :passwordNew WHERE username = :usernameOld AND password = :passwordOld LIMIT 1", nativeQuery = true)
    void updateUser(String usernameOld, String passwordOld, String usernameNew, String passwordNew);
    
    @Query(value = "SELECT COALESCE(MAX(id), 0) + 1 FROM nr_user", nativeQuery = true)
    Integer getNextId();
    
    @Modifying
    @Query(value = "INSERT INTO nr_user (id, username, password, status, gold_bar, role, activated, create_time) VALUES (:id, :username, :password, :status, :goldBar, :role, :activated, :createTime)", nativeQuery = true)
    void createUser(Integer id, String username, String password, Integer status, Integer goldBar, Integer role, Integer activated, java.sql.Timestamp createTime);
}
