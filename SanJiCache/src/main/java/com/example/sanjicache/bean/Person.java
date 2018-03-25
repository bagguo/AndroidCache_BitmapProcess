package com.example.sanjicache.bean;

/**
 * 类描述:
 * 创建人:一一哥
 * 创建时间:16/11/26 10:54
 * 备注:
 */
public class Person {
    private String name;
    private String image;

    Person(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Person() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
