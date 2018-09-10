package com.example.tarrito.notasapp.models;

import com.example.tarrito.notasapp.application.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Nota extends RealmObject{

    @PrimaryKey
    private int id;
    private String nota;
    private int color;


    public Nota() {} // Only for Realm

    public Nota(String nota, int color) {
        this.id = MyApplication.NotaID.incrementAndGet();
        this.nota = nota;
        this.color = color;

    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
