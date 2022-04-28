package Container;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class StackAndQueue {
    // 225. 用队列实现栈 用一个队列就可以！
    class MyStack {
        Queue<Integer> queue;
        int nums;
        public MyStack() {
            queue = new LinkedList<>();
            nums = 0;
        }

        public void push(int x) {
            queue.add(x);
            while (nums > 0) {
                queue.add(queue.poll());
                nums--;
            }
            nums = queue.size();
        }

        public int pop() {
            return queue.poll();

        }

        public int top() {
            return queue.peek();
        }

        public boolean empty() {
            if (queue.size() != 0)
                return false;
            return true;
        }
    }

    // 20. 有效的括号
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c:s.toCharArray()) {
            if (stack.isEmpty() && (c!='(' && c!='[' && c!='{'))
                return false;
            else if (c=='(' || c=='[' || c=='{')
                stack.push(c);
            else {
                boolean b1 = c==')' && stack.peek()=='(';
                boolean b2 = c==']' && stack.peek()=='[';
                boolean b3 = c=='}' && stack.peek()=='{';
                if (b1 || b2 || b3)
                    stack.pop();
                else
                    return false;
            }
        }
        return stack.isEmpty();
    }

    // 1047. 删除字符串中的所有相邻重复项
    public String removeDuplicates(String s) {
        Stack<Character> stack =new Stack<>();
        StringBuilder sb = new StringBuilder();
        for (char c:s.toCharArray()) {
            if (stack.isEmpty())
                stack.push(c);
            else if (c==stack.peek())
                stack.pop();
            else
                stack.push(c);
        }
        while (!stack.isEmpty())
            sb.append(stack.pop());
        return sb.reverse().toString();
    }

    // 150. 逆波兰表达式求值
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        for (String s:tokens) {
            if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")) {
                switch (s) {
                    case "+":
                        stack.push(stack.pop()+stack.pop());
                        break;
                    case "-":
                        stack.push(-stack.pop()+stack.pop());
                        break;
                    case "*":
                        stack.push(stack.pop()*stack.pop());
                        break;
                    case "/":
                        int a = stack.pop();
                        int b = stack.pop();
                        stack.push(b/a);
                        break;
                }
            }
            else {
                stack.push(Integer.parseInt(s));
            }
        }
        return stack.peek();
    }

    // 239. 滑动窗口最大值  单调队列，与窗口相关
    public int[] maxSlidingWindow(int[] nums, int k) {
        
    }

    public static void main(String[] args) {
        String[] strings = new String[] {"10","6","9","3","+","-11","*","/","*","17","+","5","+"};
        StackAndQueue saq = new StackAndQueue();
        System.out.println(saq.evalRPN(strings));
    }
}
