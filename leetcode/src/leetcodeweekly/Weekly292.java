package leetcodeweekly;

import java.util.HashSet;
import java.util.Stack;

public class Weekly292 {
    public String largestGoodInteger(String num) {
        String res = "";
        int n = num.length();
        if (n < 3)
            return res;
        int start = 0;
        int before = -1;
        while (start < n-2) {
            if (num.charAt(start) <= before) {
                start += 1;
                continue;
            }
            if (num.charAt(start+1)==num.charAt(start) && num.charAt(start+2)==num.charAt(start)) {
                res = num.substring(start, start+3);
                before = num.charAt(start);
                if (res.equals("999"))
                    return res;
            }
            else if (num.charAt(start+1)==num.charAt(start) && num.charAt(start+2)!=num.charAt(start)) {
                start += 2;
            }
            else {
                start += 1;
            }
        }
        return res;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    int sum = 0;
    public int averageOfSubtree(TreeNode root) {
        dfs(root);
        return sum;
    }
    public int[] dfs(TreeNode node) {
        if (node == null) {
            return new int[]{0, 0};
        }
        int[] left = dfs(node.left);
        int[] right = dfs(node.right);
        int a = node.val + left[0] + right[0];
        int b = 1 + left[1] + right[1];
        if (b == 1) {
            sum++;
        }
        else if (node.val == a/b) {
            sum++;
        }
        return new int[]{a, b};
    }

    public int countTexts(String pressedKeys) {
        int n = pressedKeys.length();
        if (n==1)
            return 1;
        int mod = (int) (1e9+7);
        int[] res = new int[n+1];
        res[0] = 1;
        res[1] = 1;
        for (int i=1; i<n; i++) {
            res[i+1] = res[i];
            res[i+1] = res[i+1] % mod;
            if (i-1>=0 && pressedKeys.charAt(i)==pressedKeys.charAt(i-1)) {
                res[i+1] += res[i-1];
                res[i+1] = res[i+1] % mod;
                if (i-2>=0 && pressedKeys.charAt(i)==pressedKeys.charAt(i-2)) {
                    res[i+1] += res[i-2];
                    res[i+1] = res[i+1] % mod;
                    if (pressedKeys.charAt(i)=='7' || pressedKeys.charAt(i)=='9') {
                        if (i-3>=0 && pressedKeys.charAt(i)==pressedKeys.charAt(i-3)) {
                            res[i+1] += res[i-3];
                            res[i+1] = res[i+1] % mod;
                        }
                    }
                }
            }
        }
        return res[n] % mod;
    }
    boolean check = false;
    public boolean hasValidPath(char[][] grid) {
        if (grid[0][0] == ')')
            return false;
        int n = grid.length;
        int m = grid[0].length;
        HashSet<Integer>[][] res = new HashSet[n][m];
        res[0][0] = new HashSet<>();
        res[0][0].add(1);
        for (int i=1; i<m; i++) {
            res[0][i] = new HashSet<>();
            if (grid[0][i] == '(') {
                if (res[0][i-1].isEmpty()) {
                    continue;
                }
                else {
                    for (int k:res[0][i-1]) {
                        res[0][i].add(k+1);
                    }
                }
            }
            else {
                for (int k:res[0][i-1]) {
                    if (k == 0) {
                        continue;
                    }
                    else {
                        res[0][i].add(k-1);
                    }
                }
            }
        }
        for (int i=1; i<n; i++) {
            res[i][0] = new HashSet<>();
            if (grid[i][0] == '(') {
                if (res[i-1][0].isEmpty()) {
                    continue;
                }
                else {
                    for (int k:res[i-1][0]) {
                        res[i][0].add(k+1);
                    }
                }
            }
            else {
                for (int k:res[i-1][0]) {
                    if (k == 0) {
                        continue;
                    }
                    else {
                        res[i][0].add(k-1);
                    }
                }
            }
        }
        for (int i=1; i<n; i++) {
            for (int j = 1; j < m; j++) {
                res[i][j] = new HashSet<>();
                HashSet<Integer> temp = new HashSet<>();
                temp.addAll(res[i-1][j]);
                temp.addAll(res[i][j-1]);
                if (grid[i][j] == '(') {
                    if (temp.isEmpty()) {
                        continue;
                    }
                    else {
                        for (int k:temp) {
                            res[i][j].add(k+1);
                        }
                    }
                }
                else {
                    for (int k:temp) {
                        if (k == 0) {
                            continue;
                        }
                        else {
                            res[i][j].add(k-1);
                        }
                    }
                }
            }
        }
        if (res[n-1][m-1].contains(0))
            return true;
        return false;
//        for (int i=1; i<n; i++) {
//            for (int j=1; j<m; j++) {
//                if (grid[i][j] == '(') {
//                    if (res[i-1][j]>-1 && res[i][j-1]>-1) {
//                        res[i][j] = Math.min(res[i-1][j], res[i][j-1]) + 1;
//                    }
//                    else if (res[i-1][j]>-1) {
//                        res[i][j] = res[i-1][j] + 1;
//                    }
//                    else if (res[i][j-1]>-1) {
//                        res[i][j] = res[i][j-1] + 1;
//                    }
//                    else {
//                        res[i][j] = -1;
//                    }
//                }
//                else {
//                    if (res[i-1][j]>0 && res[i][j-1]>0) {
//                        res[i][j] = Math.min(res[i-1][j], res[i][j-1]) - 1;
//                    }
//                    else if (res[i-1][j]>0) {
//                        res[i][j] = res[i-1][j] - 1;
//                    }
//                    else if (res[i][j-1]>0) {
//                        res[i][j] = res[i][j-1] - 1;
//                    }
//                    else {
//                        res[i][j] = -1;
//                    }
//                }
//            }
//        }
//        if (res[n-1][m-1]==0)
//            return true;
    }
    // 回溯+剪枝会超时，需要dpchul
    public void checkPath(char[][] grid, Stack<Character> stack, int i, int j, int n, int m) {
        if (check)
            return;
        if (stack.isEmpty() && grid[i][j]==')')
            return;
        if (i==n-1 && j==m-1) {
            if (stack.size()==1 && grid[i][j]==')') {
                check = true;
            }
            return;
        }
        if (grid[i][j]=='(') {
            stack.push('(');
        }
        else {
            stack.pop();
        }
        if (j+1 < m) {
            checkPath(grid, stack, i, j+1, n, m);
        }
        if (i+1 < n) {
            checkPath(grid, stack, i+1, j, n, m);
        }
        if (grid[i][j]=='(') {
            stack.pop();
        }
        else {
            stack.push('(');
        }
    }

    public static void main(String[] args) {
        Weekly292 w = new Weekly292();
        String s = "444444444444444444444444444444448888888888888888999999999999333333333333333366666666666666662222222222222222666666666666666633333333333333338888888888888888222222222222222244444444444444448888888888888222222222222222288888888888889999999999999999333333333444444664";
        System.out.println(w.countTexts(s));
        char[][] grid = new char[][] {{'(', '(', '('}, {')', '(', ')'}, {'(', '(', ')'}, {'(', '(', ')'}};
        grid = new char[][] {{'(', ')', ')', '(', ')', ')', ')'}};
        System.out.println(w.hasValidPath(grid));
    }
}
