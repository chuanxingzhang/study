package com.star.sync;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by jk
 * Create Date: 2020/12/5 11:52
 * Description: 三个数组轮流打印字母  LockSupport版
 */
public class LockSupportTest {
    static Thread thread2 = null;
    static Thread thread3 = null;
    static Thread thread1 = null;

    public static void main(String args[]) {

        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] letters = {"a", "b", "c", "c", "d", "e", "f", "g", "h", "i"};
        String[] capitals = {"A", "B", "C", "C", "D", "E", "F", "G", "H", "I"};

        thread1 = new Thread(() -> {
            for (int i = 0; i < numbers.length; i++) {
                System.out.println(numbers[i]);
                LockSupport.unpark(thread2);
                LockSupport.park();
            }
        });
        thread2 = new Thread(() -> {
            for (int i = 0; i < letters.length; i++) {
                LockSupport.park();
                System.out.println(letters[i]);
                LockSupport.unpark(thread3);
            }
        });
        thread3 = new Thread(() -> {
            for (int i = 0; i < capitals.length; i++) {
                LockSupport.park();
                System.out.println(capitals[i]);
                LockSupport.unpark(thread1);
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1: " + thread1.getState());
            System.out.println("t2: " + thread2.getState());
            System.out.println("t3: " + thread3.getState());
            if (thread1.getState().equals(Thread.State.TERMINATED) && thread2.getState().equals(Thread.State.TERMINATED) && thread3.getState().equals(Thread.State.TERMINATED)) {
                break;
            }
        }
    }
}
