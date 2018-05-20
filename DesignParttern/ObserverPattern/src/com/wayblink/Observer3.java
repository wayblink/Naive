package com.wayblink;

public class Observer3 extends Observer{

    public Observer3(Subject subject){
        this.subject = subject;
        this.subject.register(this);
    }

    @Override
    public void update() {
        System.out.println("Observer3: Subject update its state: " + this.subject.getState());
    }
}
