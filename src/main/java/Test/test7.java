package Test;

/**
 * @Auther：jinguangshuai
 * @Data：2022/6/9 - 06 - 09 - 10:18
 * @Description:Test
 * @version:1.0
 */
class test7 extends Thread{
    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName()+"   begin....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+"   end.....");
    }
}
class Test {
    public static void main(String[] args){
        System.out.println("main begin....");
        Thread thread = new test7();
        thread.setName("线程A");
        thread.setDaemon(true);
        thread.start();
        System.out.println("main end.....");
    }
}
