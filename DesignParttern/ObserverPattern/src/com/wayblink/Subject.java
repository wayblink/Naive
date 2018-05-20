package com.wayblink;

import java.util.ArrayList;

public class Subject {

    private int state;

    private ArrayList<Observer> registeredList = new ArrayList<>();

    public int getState(){
        return state;
    }

    public void setState(int state){
        this.state = state;
        notifyAllObservers();
    }

    public void register(Observer observer){
        registeredList.add(observer);
    }

    public void cancel(Observer observer){
        registeredList.remove(observer);
    }

    public void notifyAllObservers(){
        for(Observer observer:registeredList){
            observer.update();
        }
    }

}
