package org.lpl.demo;

public class Test {

    public static Integer i = 50;

    public  static boolean flag = true;


    public void test() throws InterruptedException {
        while (flag) {

            if (i>0) {

                Thread.sleep(500);
                i=i-1;

                String name = Thread.currentThread().getName();
                System.out.println(name+i);
                Thread.sleep(200);
                System.out.println(name+i);
            }

        }


    }

    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        new Thread(new Runnable() {

            @Override
            public void run() {


                try {
                    test.test();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = Thread.currentThread().getName();
                System.out.println(name+"---end---");
            }
        },"ta").start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    test.test();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = Thread.currentThread().getName();
                System.out.println(name+"---end---");

            }
        },"tb").start();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                flag=false;
//
//            }
//        },"tc").start();

//        Thread.sleep(5000);
//
//        flag=false;

    }
}
