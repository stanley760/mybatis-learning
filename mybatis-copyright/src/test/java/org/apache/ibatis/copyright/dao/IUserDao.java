package org.apache.ibatis.copyright.dao;

import org.apache.ibatis.copyright.entity.User;

/**
 * @author stanley
 * @description:
 * @datetime 2025-06-23 17:12
 * @version: 1.0
 */
public interface IUserDao {

    User queryUserInfoById(Integer id);
}
