package com.ch.ch;

import java.io.Serializable;

public class Book implements Serializable {


    private int _id;
    private String name;

    public Book(int _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Book{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                '}';
    }
}
