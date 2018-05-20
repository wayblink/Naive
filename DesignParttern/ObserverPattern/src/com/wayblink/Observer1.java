package com.wayblink;

public class Observer1 extends Observer{

    public Observer1(Subject subject){
        this.updateCount = 0;
        this.subject = subject;
        this.subject.register(this);
    }

    private int updateCount;

    @Override
    public void update() {
        updateCount++;
        System.out.println("Observer1: Subject have updated " + updateCount + " times");
    }
}
