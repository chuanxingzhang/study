package com.star.sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jk
 * Create Date: 2020/12/5 11:52
 * Description: 三个数组轮流打印字母  LockSupport版
 */
public class LockConditionTest {


    public static void main(String args[]) {
        Lock lock = new ReentrantLock();
        Condition numbersCondition = lock.newCondition();
        Condition lettersCondition = lock.newCondition();
        Condition capitalsCondition = lock.newCondition();
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        String[] capitals = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        Thread t1 = new Thread(() -> {
            lock.lock();
            for (int i = 0; i < numbers.length; i++) {
                System.out.println("数字：" + numbers[i]);
                try {
                    lettersCondition.signal();
                    numbersCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lettersCondition.signal();
            capitalsCondition.signal();
            System.out.println("t1结束");
            lock.unlock();

        });
        Thread t2 = new Thread(() -> {
            lock.lock();
            for (int i = 0; i < letters.length; i++) {
                System.out.println("小写字母" + letters[i]);
                try {
                    capitalsCondition.signal();
                    lettersCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                numbersCondition.signal();
                capitalsCondition.signal();
            }
            System.out.println("t2结束");
            lock.unlock();
        });
        Thread t3 = new Thread(() -> {
            lock.lock();
            for (int i = 0; i < capitals.length; i++) {
                System.out.println("大写字母：" + capitals[i]);
                try {
                    numbersCondition.signal();
                    capitalsCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            numbersCondition.signal();
            lettersCondition.signal();
            System.out.println("t3结束");
            lock.unlock();
        });
        t1.start();
        t2.start();
        t3.start();
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1: " + t1.getState());
            System.out.println("t2: " + t2.getState());
            System.out.println("t3: " + t3.getState());
            if (t1.getState().equals(Thread.State.TERMINATED) && t2.getState().equals(Thread.State.TERMINATED) && t3.getState().equals(Thread.State.TERMINATED)) {
                break;
            }
        }
    }
}
