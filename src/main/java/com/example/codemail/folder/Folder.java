package com.example.codemail.folder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Folder {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

    public Folder(String name) {
        this.name = name;
    }

    public Folder() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
