package org.apache.ibatis.copyright.entity;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-30 17:50
 * @version: 1.0
 */
public class User {
    private Integer id;

    private String user_name;

    private Integer age;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String userName) {
        this.user_name = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        String sb = "User{" + "id=" + id +
                ", user_name='" + user_name + '\'' +
                ", age=" + age +
                '}';
        return sb;
    }
}
