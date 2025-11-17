package com.chitchat.server.repository;

import com.chitchat.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {
     User save(User user);

     void delete(User user);

     void deleteById(String id);

     boolean existsByIdAndIsActiveTrue(String id);

     boolean existsByUsername(String username);

     boolean existsByEmailAndIsActiveTrue(String email);

     boolean existsByPhoneAndIsActiveTrue(String phone);

     Optional<User> findById(String id);

     Optional<User> findByIdAndIsActiveTrue(String id);

     Optional<User> findByUsername(String username);

     Optional<User> findByEmail(String email);

     Optional<User> findByEmailAndIsActiveTrue(String email);

     Optional<User> findByPhoneAndIsActiveTrue(String phoneNumber);

     Optional<User> findByResetPasswordTokenAndIsActiveTrue(String token);

     Optional<User> findByGoogleId(String googleId);

     @Query("""
               SELECT COUNT(u) FROM User u
               """)
     int countAll();

     Page<User> findByFirstNameContainingOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

     @Query("""
              SELECT u FROM User u
              LEFT JOIN Friendship f ON (f.sender = u OR f.recipient = u)
              WHERE
                (
                  LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))
                )
               AND u.isActive = true
               AND u.id != :userId
               AND (f.status IS NULL OR f.status != 'Blocked')
          """)
     Page<User> searchByAnyName(@Param("userId") String userId, @Param("name") String name, Pageable pageable);

     @Query("""
              SELECT u FROM User u
               JOIN Friendship f
               ON (f.sender = u OR f.recipient = u)
               WHERE (f.sender.id = :userId OR f.recipient.id = :userId)
               AND (
                  LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))
                )
               AND f.status = 'Accepted'
               AND u.id != :userId
               AND u.isActive = true
          """)
     Page<User> searchFriendsByName(@Param("userId") String userId, @Param("name") String name, Pageable pageable);

     @Query("""
              SELECT u FROM User u
              LEFT JOIN Friendship f ON (f.sender = u OR f.recipient = u)
              WHERE
                (
                  LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                  LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))
                )
               AND u.isActive = true
               AND (f.status IS NULL OR f.status != 'Blocked')
          """)
     Page<User> searchByAnyName(@Param("name") String name, Pageable pageable);


     // Find User's friends
     @Query("""
               SELECT u FROM User u
               JOIN Friendship f
               ON (f.sender = u OR f.recipient = u)
               WHERE (f.sender.id = :userId OR f.recipient.id = :userId)
               AND f.status = 'Accepted'
               AND u.id != :userId
               AND u.isActive = true
               """)
     Page<User> findFriends(@Param("userId") String userId, Pageable pageable);

     @Query("""
               SELECT COUNT(u) FROM User u
               JOIN Friendship f
               ON (f.sender = u OR f.recipient = u)
               WHERE (f.sender.id = :userId OR f.recipient.id = :userId)
               AND f.status = 'Accepted'
               AND u.id != :userId
               AND u.isActive = true
               """)
     int countFriends(@Param("userId") String userId);

     // Find User's friend requests
     @Query("""
               SELECT u FROM User u
               JOIN Friendship f
               ON (f.sender = u OR f.recipient = u)
               WHERE (f.sender.id = :userId OR f.recipient.id = :userId)
               AND f.status = 'Pending'
               AND u.id != :userId
               AND u.isActive = true
               """)
     Page<User> findFriendRequests(@Param("userId") String userId, Pageable pageable);
     
     @Query("""
               SELECT COUNT(u) FROM User u
               JOIN Friendship f
               ON (f.sender = u OR f.recipient = u)
               WHERE (f.sender.id = :userId OR f.recipient.id = :userId)
               AND f.status = 'Pending'
               AND u.id != :userId
               """)
     int countFriendRequests(@Param("userId") String userId);

     // Find random users: not friends, not those sent reuquest to current user
     @Query("""
               SELECT u FROM User u
               WHERE u.id != :userId
                    AND u.username != 'ADMIN'
                    AND NOT EXISTS (
                    SELECT f FROM Friendship f
                    WHERE
                         (
                              (f.sender.id = :userId AND f.recipient.id = u.id)
                              OR (f.recipient.id = :userId AND f.sender.id = u.id)
                         )
                         AND f.status = 'Accepted'
                    )
                    AND NOT EXISTS (
                    SELECT f FROM Friendship f
                    WHERE
                         f.sender.id = u.id AND f.recipient.id = :userId
                         AND f.status = 'Pending'
                    )
                    AND NOT EXISTS (
                    SELECT f FROM Friendship f
                    WHERE
                         (
                              (f.sender.id = :userId AND f.recipient.id = u.id)
                              OR (f.recipient.id = :userId AND f.sender.id = u.id)
                         )
                         AND f.status = 'Blocked'
                    )
               AND u.isActive = true
               """)
     Page<User> findRandomUsers(@Param("userId") String userId, Pageable pageable);
     
	// Friend suggestion by finding other users having most mutual friends
     @Query("""
               SELECT u FROM User u
               WHERE u.id != :userId
               AND u.id NOT IN (
                    SELECT f.recipient.id FROM Friendship f WHERE f.sender.id = :userId
                    UNION
                    SELECT f2.sender.id FROM Friendship f2 WHERE f2.recipient.id = :userId
               )
               AND u.id IN (
                    SELECT f3.recipient.id FROM Friendship f3
                    WHERE f3.sender.id IN (
                         SELECT f4.recipient.id FROM Friendship f4 WHERE f4.sender.id = :userId
                         UNION
                         SELECT f5.sender.id FROM Friendship f5 WHERE f5.recipient.id = :userId
                    )
                    UNION
                    SELECT f6.sender.id FROM Friendship f6
                    WHERE f6.recipient.id IN (
                         SELECT f7.recipient.id FROM Friendship f7 WHERE f7.sender.id = :userId
                         UNION
                         SELECT f8.sender.id FROM Friendship f8 WHERE f8.recipient.id = :userId
                    )
               )
               AND u.isActive = true
               GROUP BY u.id
		""")
     Page<User> findSuggestedFriends(@Param("userId") String userId, Pageable pageable);

     // 2 User's mutual friends
//     @Query("""
//               SELECT u FROM User u
//               WHERE u.id IN (
//                    SELECT f.recipient.id FROM Friendship f
//                    WHERE f.sender.id = :userAId AND f.status = 'Accepted'
//                    AND f.recipient.id IN (
//                         SELECT f2.recipient.id FROM Friendship f2
//                         WHERE f2.sender.id = :userBId AND f2.status = 'Accepted'
//                    )
//               )
//               OR u.id IN (
//                    SELECT f3.sender.id FROM Friendship f3
//                    WHERE f3.recipient.id = :userAId AND f3.status = 'Accepted'
//                    AND f3.sender.id IN (
//                         SELECT f4.sender.id FROM Friendship f4
//                         WHERE f4.recipient.id = :userBId AND f4.status = 'Accepted'
//                    )
//               )
//               """)
//     Page<User> findMutualFriends(@Param("userAId") Long userAId,
//                              @Param("userBId") Long userBId,
//                              Pageable pageable);

     @Query("""
         SELECT u FROM User u
         WHERE u.id IN (
             SELECT
                 CASE
                     WHEN f.sender.id = :userId1 THEN f.recipient.id
                     ELSE f.sender.id
                 END
             FROM Friendship f
             WHERE (f.sender.id = :userId1 OR f.recipient.id = :userId1)
               AND f.status = 'Accepted'
         )
         AND u.id IN (
             SELECT
                 CASE
                     WHEN f.sender.id = :userId2 THEN f.recipient.id
                     ELSE f.sender.id
                 END
             FROM Friendship f
             WHERE (f.sender.id = :userId2 OR f.recipient.id = :userId2)
               AND f.status = 'Accepted'
         )
         AND u.isActive = true
     """)
     Page<User> findMutualFriends(@Param("userId1") String userId1, @Param("userId2") String userId2, Pageable pageable);

     @Query("""
    SELECT u FROM User u
    WHERE u.id IN (
        SELECT
            CASE
                WHEN f.sender.id = :userId1 THEN f.recipient.id
                ELSE f.sender.id
            END
        FROM Friendship f
        WHERE (f.sender.id = :userId1 OR f.recipient.id = :userId1)
          AND f.status = 'Accepted'
    )
    AND u.id IN (
        SELECT
            CASE
                WHEN f.sender.id = :userId2 THEN f.recipient.id
                ELSE f.sender.id
            END
        FROM Friendship f
        WHERE (f.sender.id = :userId2 OR f.recipient.id = :userId2)
          AND f.status = 'Accepted'
    )
""")
     int countMutualFriends(@Param("userId1") String userId1, @Param("userId2") String userId2);

     // Count 2 User's mutual friends
//     @Query("""
//               SELECT COUNT(f) FROM Friendship f
//               WHERE f.status = 'Accepted' AND (
//                    (f.sender.id = :userAId AND f.recipient.id IN (
//                         SELECT f2.recipient.id FROM Friendship f2
//                         WHERE f2.sender.id = :userBId AND f2.status = 'Accepted'
//                    ))
//                    OR
//                    (f.recipient.id = :userAId AND f.sender.id IN (
//                         SELECT f3.sender.id FROM Friendship f3
//                         WHERE f3.recipient.id = :userBId AND f3.status = 'Accepted'
//                    ))
//               )
//          """)
//     int countMutualFriends(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

     // Count mutual friends for a user and a list of users
//     @Query("""
     ////          SELECT
     ////              CASE
     ////                  WHEN f.sender.id = :userId THEN f.recipient.id
     ////                  ELSE f.sender.id
     ////              END,
     ////              COUNT(f)
     ////          FROM Friendship f
     ////          WHERE f.status = 'Accepted' AND (
     ////              (f.sender.id = :userId AND f.recipient.id IN :otherIds)
     ////              OR
     ////              (f.recipient.id = :userId AND f.sender.id IN :otherIds)
     ////          )
     ////          GROUP BY
     ////              CASE
     ////                  WHEN f.sender.id = :userId THEN f.recipient.id
     ////                  ELSE f.sender.id
     ////              END
     ////          """)
     ////     List<Object[]> countMutualFriendsForUsers(@Param("userId") Long userId,
     ////                                             @Param("otherIds") List<Long> otherIds);

     @Query(value = """
         SELECT u2.user_id AS other_user_id, COUNT(*) AS mutual_friend_count
         FROM (
             -- Lấy danh sách bạn bè của userId
             SELECT
                 CASE
                     WHEN f.sender_id = :userId THEN f.recipient_id
                     ELSE f.sender_id
                 END AS friend_id
             FROM friendships f
             WHERE (f.sender_id = :userId OR f.recipient_id = :userId)
               AND f.status = 'Accepted'
         ) AS u1
         JOIN (
             -- Lặp qua mỗi user khác trong danh sách, lấy bạn của họ
             SELECT
                 CASE
                     WHEN f2.sender_id = u.id THEN f2.recipient_id
                     ELSE f2.sender_id
                 END AS friend_id,
                 u.id AS user_id
             FROM users u
             JOIN friendships f2
               ON (f2.sender_id = u.id OR f2.recipient_id = u.id)
             WHERE u.id IN :otherIds
               AND f2.status = 'Accepted'
               AND u.is_active = true
         ) AS u2
           ON u1.friend_id = u2.friend_id
         GROUP BY u2.user_id
     """, nativeQuery = true)
          List<Object[]> countMutualFriendsForUsers(
                  @Param("userId") String userId,
                  @Param("otherIds") List<String> otherIds
          );




     @Query("SELECT u FROM User u " +
             "WHERE u.email = :loginInput OR u.phone = :loginInput ")
     Optional<User> findByEmailOrPhone(@Param("loginInput") String loginInput);
               
     @Query(value = "SELECT * FROM users u " +
             "WHERE u.refresh_token = :token " +
             "AND (u.email = :emailUsernamePhone OR u.username = :emailUsernamePhone OR u.phone = :emailUsernamePhone) " +
             "AND u.is_active = 1", nativeQuery = true)
     Optional<User> findByRefreshTokenAndEmailOrUsernameOrPhone(@Param("token") String token, 
               @Param("emailUsernamePhone") String emailUsernamePhone);
}
