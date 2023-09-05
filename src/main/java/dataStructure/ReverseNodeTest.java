package dataStructure;

/**
 * @Auther：jinguangshuai
 * @Data：2023/3/8 - 03 - 08 - 15:05
 * @Description:dataStructure
 * @version:1.0
 */
public class ReverseNodeTest {
    //单向链表
    public class Node{
        public int value;
        public Node next;

        public Node(int data){
            value = data;
        }
    }

    public Node reverseNode(Node head){
        Node pre = null;
        Node next = null;
        while(head != null){
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }


    //双向链表
    public class DoubleNode{
        int value;
        DoubleNode last;
        DoubleNode next;
        public DoubleNode(int data){
            value = data;
        }
    }

    public DoubleNode reverseDoubleNode(DoubleNode head){
        DoubleNode pre = null;
        DoubleNode next = null;

        while(head != null){
            next = head.next;
            head.next = pre;
            head.last = next;
            pre = head;
            head = next;
        }

        return pre;
    }


}
