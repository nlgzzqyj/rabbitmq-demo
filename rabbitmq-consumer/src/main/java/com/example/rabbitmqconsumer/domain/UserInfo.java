package com.example.rabbitmqconsumer.domain;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String username;
    private String sex;
    private Integer age;

    public UserInfo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
