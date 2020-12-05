package com.star.sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
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
        AtomicInteger times = new AtomicInteger(1);
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        String[] capitals = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        Thread t1 = new Thread(() -> {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "获取了锁");
            Boolean fistIsThree = false;
            if (times.intValue() != 3) {
                times.incrementAndGet();
            } else {
                fistIsThree = true;
            }
            for (int i = 0; i < numbers.length; i++) {
                try {
                    if (fistIsThree) {
                        fistIsThree = false;
                    } else {
                        numbersCondition.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("数字：" + numbers[i]);
                lettersCondition.signal();
            }
            lettersCondition.signal();
            capitalsCondition.signal();
            System.out.println("t1结束");
            lock.unlock();

        });
        Thread t2 = new Thread(() -> {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "获取了锁");
            if (times.intValue() == 3) {
                numbersCondition.signal();
            } else {
                times.incrementAndGet();
            }
            for (int i = 0; i < letters.length; i++) {
                try {
                    lettersCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("小写字母" + letters[i]);
                capitalsCondition.signal();

                numbersCondition.signal();
                capitalsCondition.signal();
            }
            System.out.println("t2结束");
            lock.unlock();
        });
        Thread t3 = new Thread(() -> {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "获取了锁");
            if (times.intValue() == 3) {
                numbersCondition.signal();
            } else {
                times.incrementAndGet();
            }
            for (int i = 0; i < capitals.length; i++) {
                try {
                    capitalsCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("大写字母：" + capitals[i]);
                numbersCondition.signal();

            }
            numbersCondition.signal();
            lettersCondition.signal();
            System.out.println("t3结束");
            lock.unlock();
        });
        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t1.start();
        t2.start();
        t3.start();
        while (true) {
            try {
                Thread.sleep(3000);
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
