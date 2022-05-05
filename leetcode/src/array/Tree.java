package array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Tree {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        public TreeNode () {}
        public TreeNode (int val) {
            this.val = val;
        }
        public TreeNode (int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }

    }

    // 144. 二叉树的前序遍历 递归方法
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        preorder(root, res);
        return res;
    }
    public void preorder(TreeNode node, List<Integer> list) {
        if (node != null) {
            list.add(node.val);
            preorder(node.left, list);
            preorder(node.right, list);
        }
    }
    // 144. 二叉树的前序遍历 用栈来解决
    public List<Integer> preorderTraversal1(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        if (root == null)
            return res;
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            res.add(node.val);
            if (node.right != null)
                stack.push(node.right);
            if (node.left != null)
                stack.push(node.left);
        }
        return res;
    }

    // 94. 二叉树的中序遍历，递归方法
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        inorder(root, res);
        return res;

    }
    public void inorder(TreeNode node, List<Integer> list) {
        if (node != null) {
            inorder(node.left, list);
            list.add(node.val);
            inorder(node.right, list);
        }
    }
    // 94. 二叉树的中序遍历，用栈来解决，两个while循环，较难
    public List<Integer> inorderTraversal1(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        if (root == null)
            return res;
        stack.push(root);
        while (!stack.isEmpty()) {
            if (stack.peek().left != null) {
                stack.push(stack.peek().left);
                continue;
            }
            while (!stack.isEmpty()){
                TreeNode node = stack.pop();
                res.add(node.val);
                if (node.right != null) {
                    stack.push(node.right);
                    break;
                }
            }
        }
        return res;
    }

    // 145. 二叉树的后序遍历，递归方法
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        postorder(root, res);
        return res;
    }
    public void postorder(TreeNode node, List<Integer> list) {
        if (node != null) {
            postorder(node.left, list);
            postorder(node.right, list);
            list.add(node.val);
        }
    }
    // 145. 二叉树的后序遍历，用栈解决，修改版的前序遍历，然后再反转数组
    public List<Integer> postorderTraversal1(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root ==null)
            return res;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            res.add(node.val);
            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
        Collections.reverse(res);
        return res;
    }

}
