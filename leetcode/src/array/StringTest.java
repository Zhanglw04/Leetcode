package array;

import java.util.Stack;

public class StringTest {
    // 344 反转字符串
    public void reverseString(char[] s) {
        int left = 0;
        int right = s.length-1;
        while (right>left) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            right--;
            left++;
        }
    }

    // 541 反转字符串 II
    public String reverseStr(String s, int k) {
        int n = s.length();
        int m = n / k;
        char[] chars = s.toCharArray();
        for (int i=0; i<=m; i=i+2) {
            int left = i*k+0;
            int right = i*k+k-1<n ? i*k+k-1 : n-1;
            while (right>left) {
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;
                right--;
                left++;
            }
        }
        return new String(chars);
    }

    // 剑指 Offer 05. 替换空格
    public String replaceSpace(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c:s.toCharArray()) {
            if (c==' ') {
                sb.append("%20");
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //151. 颠倒字符串中的单词
    public String reverseWords(String s) {
        String[] strings = s.trim().split("[/s]+");
        int n = strings.length;
        StringBuilder sb = new StringBuilder();
        for (int i=n-1; i>=0; i--) {
            sb.append(strings[i]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    // 剑指 Offer 58 - II. 左旋转字符串
    public String reverseLeftWords(String s, int n) {
        int m = s.length();
        String before = s.substring(0, n);
        String after = s.substring(n, m);
        return after.concat(before);
    }

    // 28. 实现 strStr()
    public int strStr(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();
        if (m==0)
            return 0;
        if (n<m)
            return -1;
        for (int i=0; i<n; i++) {
            int temp = i;
            int j=0;
            while (j<m) {
                if (i<n && haystack.charAt(i) != needle.charAt(j)) {
                    break;
                }
                i++;
                j++;
            }
            if (j==m)
                return temp;
            i = temp;
        }
        return -1;
    }
    // 用kmp算法
    public int strStr1(String haystack, String needle) {
        int m = needle.length();
        int n = haystack.length();
        int j = -1;
        int[] next = new int[m];
        next[0] = j;
        for (int i=1; i<m; i++) {
            while (j>=0 && needle.charAt(i)!=needle.charAt(j+1)) {
                j = next[j];
            }
            if (needle.charAt(i)==needle.charAt(j+1)) {
                j++;
            }
            next[i] = j;
        }
        j = -1;
        for (int i=0; i<n; i++) {
            while (j>=0 && haystack.charAt(i)!=needle.charAt(j+1)) {
                j = next[j];
            }
            if (haystack.charAt(i)==needle.charAt(j+1)) {
                j++;
            }
            if (j==m-1) {
                return i-m+1;
            }
        }
        return -1;
    }

    // 459. 重复的子字符串 kmp算法求字符串的最长相同前后缀数组next
    public boolean repeatedSubstringPattern(String s) {
        int n = s.length();
        int[] next =new int[n];
        int j = -1;
        next[0] = j;
        for (int i=1; i<n; i++) {
            while (j>=0 && s.charAt(i)!=s.charAt(j+1)) {
                j = next[j];
            }
            if (s.charAt(i)==s.charAt(j+1)) {
                j++;
            }
            next[i] = j;
        }
        // 当 n % (n - (next[n-1] + 1)) == 0 时，说明s由重复当子字符串组成
        if (next[n-1]!=-1 && n%(n-(next[n-1]+1))==0)
            return true;
        return false;
    }

    public static void main(String[] args) {
        String s1 = "mississippi";
        String s2 = "issipi";
        StringTest st = new StringTest();
        System.out.println(st.strStr1(s1, s2));
        System.out.println(st.repeatedSubstringPattern("abaababaab"));
        Stack<Integer> stack = new Stack<>();
        System.out.println(stack.peek());

    }
}
