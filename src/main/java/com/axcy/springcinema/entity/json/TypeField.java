package com.axcy.springcinema.entity.json;

/**
 * @author Aleksei_Cherniavskii
 */
public enum TypeField {
    USERS("users"), EVENTS("events");

    private String name;

    TypeField(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
