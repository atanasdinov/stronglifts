package sl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sl.model.User;

import java.util.Optional;

/**
 * <b>This interface declares manipulations over the {@link User} data in the database.</b>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * This method is used to get {@link User} by its token.
     *
     * @param token used for authentication
     * @return {@link User}
     */
    @Query("select u from User u where u.token=:token")
    Optional<User> findByToken(@Param("token") String token);

    /**
     * This method is used to get {@link User} by its username.
     *
     * @param username of the user
     * @return {@link User}
     */
    @Query("select u from User u where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * This method is used to set authentication token for the given {@link User}.
     *
     * @param username of the user
     * @param token    value to be saved in the database
     */
    @Modifying
    @Query("update User u set auth_token=:token where username=:username")
    void setToken(@Param("username") String username, @Param("token") String token);
}
