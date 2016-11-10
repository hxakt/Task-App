package com.rakesh.mukherjee.teskapp.utils;

/**
 * Created by 100384 on 11/8/2016.
 */

public class Tasks {
    int _id;
    String _name;
    int _state;

    public Tasks(){

    }

    public Tasks(int id, String name, int state){
        this._id = id;
        this._name = name;
        this._state = state;
    }

    public void setID(int id){
        this._id = id;
    }

    public void setName(String name){
        this._name = name;
    }

    public void setState(int state){
        this._state = state;
    }

    public int getID(){
        return _id;
    }

    public String getName(){
        return _name;
    }

    public int getState(){
        return _state;
    }
}
