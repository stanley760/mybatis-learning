package org.apache.ibatis.debug.mapper;

import org.apache.ibatis.debug.entity.User;

/**
 * @author ywb
 * @description:
 * @datetime 2024-08-19 10:00
 * @version: 1.0
 */
public interface UserMapper {

    User getUserById(Long id);

    User getUserByName(String userName);

    int saveUser(User user);

    int deleteUser(Long id);
}
