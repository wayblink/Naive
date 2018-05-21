package com.wayblink;

public class    Main {

    public static void main(String[] args){
        Main main = new Main();
        main.call1();
    }

    public void call1(){
        call2();
    }


    public void call2(){
        call3();
    }


    public void call3(){
        call4();
    }


    public void call4(){
        System.out.print(StackPrinter.printTrack());
    }
}
