package com.hmis.server.hmis.common.user.repository;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Permission;
import com.hmis.server.hmis.common.common.model.Role;
import com.hmis.server.hmis.common.user.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional< User > findByUserNameIgnoreCase(String username);

    Optional<User> findByUserName(String username);

    List<User> findByDepartment(Department department);

    Optional<User> findByEmail(String email);

    List<User> findAllByUserNameContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String userName, String firstName, String lastName);

    @Transactional
    @Modifying
    @Query("update User u set u.accountNonLocked = :status where u.userName = :username")
    void updateUserAccountNonLocked(@Param("username") String username, @Param("status") boolean isAccountNonLocked);

    @Transactional
    @Modifying
    @Query("update User u set u.enabled = :status where u.userName = :username")
    void updateUserAccountEnabled(@Param("username") String username, @Param("status") boolean isAccountEnabled);

    @Transactional
    @Modifying
    @Query("update User u set u.tokenNotExpired = :status where u.userName = :username")
    void updateUserTokenNotExpired(@Param("username") String username, @Param("status") boolean isTokenNotExpired);

    @Transactional
    @Modifying
    @Query("update User u set u.accountNotExpired = :status where u.userName = :username")
    void updateUserAccountNotExpired(@Param("username") String username, @Param("status") boolean isAccountNotExpired );

    @Transactional
    @Modifying
    @Query(value = "update User u set u.password = :password where  u.id = :id")
    int updateUserPassword(@Param("password") String password, @Param("id") long id);

    List<User> findAllByRolesPermissionsIn(List<Permission> permissions);

    HashSet<User> findAllByRolesIn(List<Role> roles);
}

