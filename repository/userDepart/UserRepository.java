package com.bdi.sselab.repository.userDepart;

import com.bdi.sselab.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wh on 2019/3/25.
 */
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
