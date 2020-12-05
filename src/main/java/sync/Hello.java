package sync;

/**
 * Created by jk
 * Create Date: 2020/12/5 11:52
 * Description: 测试
 */
public class Hello {
    int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    String[] letters = {"a", "b", "c", "c", "d", "e", "f", "g", "h", "i"};
    String[] capitals = {"A", "B", "C", "C", "D", "E", "F", "G", "H", "I"};

    private Object object = new Object();

    public void testSynchronized() {
        Thread thread1 = new Thread(() -> {
            synchronized (object) {
                for (int i = 0; i < numbers.length; i++) {
                    System.out.println(numbers[i]);
                    try {
                        object.notify();
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (object) {
                for (int i = 0; i < letters.length; i++) {
                    System.out.println(letters[i]);
                    try {
                        object.notify(); //叫醒其他线程，这里是t1
                        object.wait(); //让自己阻塞，让出锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
//
//        Thread thread3 = new Thread(() -> {
//            synchronized (object) {
//                for (int i = 0; i < capitals.length; i++) {
//                    System.out.println(capitals[i]);
//                    try {
//                        object.notify(); //叫醒其他线程，这里是t1
//                        object.wait(); //让自己阻塞，让出锁
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

//        });
        thread1.start();
        thread2.start();
//        thread3.start();
    }

    public static void main(String[] args) {
        Hello hello = new Hello();
        hello.testSynchronized();
    }

}
