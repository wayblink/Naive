package com.wayblink;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class Main {

    public static void main(String[] args) {

        Subject subject = new Subject();

        Observer1 observer1 = new Observer1(subject);

        Observer2 observer2 = new Observer2(subject);

        Observer3 observer3 = new Observer3(subject);

        System.out.println("Subject: initial state: " + subject.getState());

        subject.setState(1);

        subject.setState(2);

        subject.setState(3);


    }
}
