package Test;

/**
 * @Auther：jinguangshuai
 * @Data：2023/2/24 - 02 - 24 - 17:03
 * @Description:Test
 * @version:1.0
 */
public class test13 {



    public static void getTwo(int[] arr){

        int eor = 0;
        for (int i = 0; i < arr.length; i++) {
            eor = eor ^ arr[i];
        }

        //eor = a^b
        int eors = eor & ((~eor) + 1);
        int rightOne = 0;
        for (int i = 0; i < arr.length; i++) {
            if((eors & arr[i]) != 0){
                rightOne = rightOne^arr[i];
            }
        }
        int lastOne = rightOne ^ eor;

        System.out.println(rightOne);
        System.out.println(lastOne);

    }

    public static void main(String[] args) {
        int[] arr2 = { 4, 3, 4, 2, 2, 2, 4, 1, 1, 1, 3, 3, 1, 1, 1, 4, 2, 2 };
        getTwo(arr2);
    }
}
