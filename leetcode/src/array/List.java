package array;

import java.util.HashMap;

public class List {
    public class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    // 203 删除链表元素
    public ListNode removeElements(ListNode head, int val) {
        if (head==null)
            return head;
        ListNode prehead = new ListNode();
        prehead.next = head;
        ListNode pre = prehead;
        ListNode cur = head;
        while (cur != null) {
            if (cur.val == val) {
                pre.next = cur.next;
                cur = pre.next;
            }
            else {
                pre = pre.next;
                cur = cur.next;
            }
        }
        return prehead.next;
    }


    class LinkedNode {
        int val;
        LinkedNode next;
        LinkedNode pre;
        public LinkedNode () {}
        public LinkedNode (int val) {
            this.val = val;
        }
        public LinkedNode (int val, LinkedNode pre, LinkedNode next) {
            this.val = val;
            this.pre = pre;
            this.next = next;
        }
    }
    class MyLinkedList {
        LinkedNode head;
        LinkedNode tail;
        int len;
        public MyLinkedList() {
            head = new LinkedNode();
            tail = new LinkedNode();
            head.next = tail;
            tail.pre = head;
            len = 0;
        }

        public int get(int index) {
            if (index>=len || index<0)
                return -1;
            int cnt = 0;
            LinkedNode cur = head.next;
            while (cnt<index) {
                cur = cur.next;
                cnt++;
            }
            return cur.val;
        }

        public void addAtHead(int val) {
            LinkedNode node = new LinkedNode(val);
            LinkedNode cur = head.next;
            node.pre = head;
            node.next = cur;
            head.next = node;
            cur.pre = node;
            len++;
        }

        public void addAtTail(int val) {
            LinkedNode node = new LinkedNode(val);
            LinkedNode cur = tail.pre;
            node.pre = cur;
            node.next = tail;
            tail.pre = node;
            cur.next = node;
            len++;
        }

        public void addAtIndex(int index, int val) {
            if (index<0) {
                addAtHead(val);
                return;
            }
            if (index>len)
                return;
            if (index==len) {
                addAtTail(val);
                return;
            }
            int cnt = 0;
            LinkedNode cur = head.next;
            while (cnt<index) {
                cur = cur.next;
                cnt++;
            }
            LinkedNode node = new LinkedNode(val);
            node.pre = cur.pre;
            cur.pre.next = node;
            node.next = cur;
            cur.pre =node;
            len++;
        }

        public void deleteAtIndex(int index) {
            if (index>=len || index<0)
                return;
            int cnt = 0;
            LinkedNode cur = head.next;
            while (cnt<index) {
                cur = cur.next;
                cnt++;
            }
            cur.pre.next = cur.next;
            cur.next.pre = cur.pre;
            len--;
        }
    }

    // 19 删除倒数第n个节点， 双指针法， first与second相差n个点
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (n<=0 || head==null)
            return head;
        ListNode preHead = new ListNode();
        preHead.next = head;
        ListNode first = preHead;
        ListNode second = preHead;
        int cnt = 0;
        while (second!=null && cnt<n) {
            second = second.next;
            cnt++;
        }
        if (second==null)
            return head;
        while (second.next!=null){
            first = first.next;
            second = second.next;
        }
        first.next = first.next.next;
        return preHead.next;
    }
    public ListNode removeNthFromEnd1(ListNode head, int n) {
        ListNode cur = head;
        int len = 0;
        while (cur != null) {
            len += 1;
            cur = cur.next;
        }
        cur = head;
        int x = len - n - 1;
        if(x < 0) {
            return head.next;
        }
        while (x-- > 0) {
            cur = cur.next;
        }
        cur.next = cur.next.next;
        return head;
    }

    // 面试题0207 链表相交
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode nodeA = headA;
        ListNode nodeB = headB;
        while (nodeA!=null || nodeB!=null){
            if (nodeA==nodeB) {
                return nodeA;
            }
            else {
                nodeA = nodeA!=null ? nodeA.next : headB;
                nodeB = nodeB!=null ? nodeB.next : headA;
            }
        }
        return null;
    }

    // 142 环形链表 快慢指针
    public ListNode detectCycle(ListNode head) {
        if (head==null)
            return null;
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null) {
            slow = slow.next;
            if (fast.next != null) {
                fast = fast.next.next;
            }
            else {
                return null;
            }
            if (slow == fast) {
                slow = head;
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }
                return fast;
            }
        }
        return null;
    }

}
