package Test2;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther：jinguangshuai
 * @Data：2023/6/15 - 06 - 15 - 14:20
 * @Description:Test2
 * @version:1.0
 */
public class test01 {
    public static void main(String[] args) {
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("第一个异步任务");
        });

        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("第二个异步任务");
        });
        CompletableFuture.allOf(f1,f2).join();
        System.out.println("CompletableFuture Test runAsync");
    }

}
