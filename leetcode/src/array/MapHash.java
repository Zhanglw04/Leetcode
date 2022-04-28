package array;

import java.util.*;
import java.util.List;

public class MapHash {

    // 242 字母异位词 数组替代hashmap
    public boolean isAnagram(String s, String t) {
        int n = s.length();
        int m = t.length();
        if (n != m)
            return false;
        int[] nums = new int[26];
        for (int i=0; i<n; i++) {
            nums[s.charAt(i)-'a']++;
        }
        for (int i=0; i<m; i++) {
            nums[t.charAt(i) - 'a']--;
        }
        for (int i=0; i<26; i++) {
            if (nums[i] != 0)
                return false;
        }
        return true;
    }

    // 1002 查找公共字符， boolean数组
    public List<String> commonChars(String[] words) {
        List<String> res = new ArrayList<>();
        int n = words.length;
        int[][] nums = new int[n][26];
        for (int i=0; i<n; i++) {
            for (char c:words[i].toCharArray()) {
                nums[i][c-'a']++;
            }
        }
        for (int i=1; i<n; i++) {
            for (int j=0; j<26; j++) {
                nums[0][j] = Math.min(nums[0][j], nums[i][j]);
            }
        }
        for (int j=0; j<26; j++) {
            if (nums[0][j]>0) {
                char c = (char) (j + 'a');
                for (int i=0; i<nums[0][j]; i++) {
                    res.add(String.valueOf(c));
                }
            }
        }
        return res;
    }

    // 349 两个数组的交集
    public int[] intersection(int[] nums1, int[] nums2) {
        List<Integer> list = new ArrayList<>();
        HashSet<Integer> set1 = new HashSet<>();
        for (int i:nums1) {
            set1.add(i);
        }
        for (int i:nums2) {
            if (set1.contains(i)) {
                list.add(i);
                set1.remove(i);
            }
        }
        int[] res = new int[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i] = list.get(i);
        return res;
    }

    // 202 快乐数
    public boolean isHappy(int n) {
        int a = getHappyNum(n);
        HashSet<Integer> set = new HashSet<>();
        while (a != 1) {
            if (set.contains(a))
                return false;
            else {
                set.add(a);
                a = getHappyNum(a);
            }
        }
        return true;
    }
    public int getHappyNum(int n) {
        int num = 0;
        while (n != 0) {
            int a = n%10;
            num += a*a;
            n = n/10;
        }
        return num;
    }

    // 454 四数相加
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        HashMap<Integer, Integer> map1 = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();
        int n = nums1.length;
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++) {
                int num = nums1[i]+nums2[j];
                map1.put(num, map1.getOrDefault(num, 0)+1);
            }
        }
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++) {
                int num = nums3[i]+nums4[j];
                map2.put(num, map2.getOrDefault(num, 0)+1);
            }
        }
        int sum= 0;
        for (int i:map1.keySet()) {
            if (map2.containsKey(-i)) {
                sum += map1.get(i)*map2.get(-i);
            }
        }
        return sum;
    }

    // 383. 赎金信
    public boolean canConstruct(String ransomNote, String magazine) {
        HashMap<Character, Integer> map = new HashMap<>();
        for (char c:magazine.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0)+1);
        }
        for (char c:ransomNote.toCharArray()) {
            if (!map.containsKey(c)) {
                return false;
            }
            else {
                int num = map.get(c)-1;
                if (num==0) {
                    map.remove(c);
                }
                else {
                    map.put(c, num);
                }
            }
        }
        return true;
    }

    // 15 三数之和，用双指针法，因为不能有重复
    public List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        List<List<Integer>> lists = new ArrayList<>();
        Arrays.sort(nums);
        for (int i=0; i<n-2; i++) {
            if (nums[i]>0) {
                return lists;
            }
            if (i>0 && nums[i]==nums[i-1]) {
                continue;
            }
            int left = i+1;
            int right = n-1;
            while (right > left) {
                int sum = nums[i] + nums[left] +nums[right];
                if (sum > 0) {
                    right--;
                }
                else if (sum <0) {
                    left++;
                }
                else {
                    List<Integer> list = new ArrayList<>();
                    list.add(nums[i]);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    lists.add(list);
                    while (right>left && nums[right]==nums[right-1]) {
                        right--;
                    }
                    while (right>left && nums[left]==nums[left+1]) {
                        left++;
                    }
                    right--;
                    left++;
                }

            }
        }
        return lists;
    }

    // 18 四数之和
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> lists = new ArrayList<>();
        int n = nums.length;
        Arrays.sort(nums);
        for (int i=0; i<n-3; i++) {
            if (target>=0 && nums[i]>target) {
                return lists;
            }
            if (i>0 && nums[i]==nums[i-1]) {
                continue;
            }
            for (int j=i+1; j<n-2; j++) {
                if (target-nums[i]>=0 && nums[j]>target-nums[i]) {
                    break;
                }
                if (j>i+1 && nums[j]==nums[j-1]) {
                    continue;
                }
                int left = j+1;
                int right = n-1;
                while (right > left) {
                    int sum = nums[i]+nums[j]+nums[left]+nums[right];
                    if (sum > target) {
                        right--;
                    }
                    else if (sum < target) {
                        left++;
                    }
                    else {
                        lists.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        while (right>left && nums[right]==nums[right-1]) {
                            right--;
                        }
                        while (right>left && nums[left]==nums[left+1]) {
                            left++;
                        }
                        right--;
                        left++;
                    }
                }
            }
        }
        return lists;
    }


}
