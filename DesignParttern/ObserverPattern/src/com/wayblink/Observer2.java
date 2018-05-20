package com.wayblink;

public class Observer2 extends Observer{

    public Observer2(Subject subject){
        this.subject = subject;
        this.subject.register(this);
    }

    @Override
    public void update() {
        System.out.println("Observer2: Subject update");
    }
}
