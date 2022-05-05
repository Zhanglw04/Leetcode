package leetcodeweekly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Weekly291 {
    public String removeDigit(String number, char digit) {
        int n = number.length();
        for (int i=0; i<n-1; i++) {
            if (number.charAt(i)==digit) {
                if (number.charAt(i + 1) > number.charAt(i)) {
                    return number.substring(0, i).concat(number.substring(i + 1, n));
                }
            }
        }
        for (int i=n-1; i>=0; i--) {
            if (number.charAt(i)==digit) {
                return number.substring(0, i).concat(number.substring(i+1, n));
            }
        }
        return number;
    }

    public int minimumCardPickup(int[] cards) {
        int n = cards.length;
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        for (int i=0; i<n; i++) {
            List<Integer> list = map.getOrDefault(cards[i], new ArrayList<>());
            list.add(i);
            map.put(cards[i], list);
        }
        int min = n+1;
        for (int i:map.keySet()) {
            if (map.get(i).size()>1) {
                for (int j=1; j<map.get(i).size(); j++) {
                    min = Math.min(min, map.get(i).get(j)-map.get(i).get(j-1)+1);
                }
            }
        }
        if (min == n+1)
            return -1;
        return min;
    }

    public int countDistinct(int[] nums, int k, int p) {
        int n = nums.length;
        int left = 0;
        int right = 0;
        HashSet<List<Integer>> sets = new HashSet<>();
        List<Integer> list;
        int cnt = 0;
        while (right < n) {
            if (nums[right]%p==0) {
                cnt++;
            }
            if (cnt<k+1 && right==n-1) {
                list = new ArrayList<>();
                for (int i=left; i<=right; i++){
                    list.add(nums[i]);
                }
                sets.add(list);
                break;
            }
            if (cnt==k+1) {
                list = new ArrayList<>();
                for (int i=left; i<right; i++){
                    list.add(nums[i]);
                }
                sets.add(list);
                right--;
                cnt--;
                while (left<=right && nums[left]%p != 0) {
                    left++;
                }
                left++;
                cnt--;
            }
            right++;
        }
        HashSet<List<Integer>> set1 = new HashSet<>();
        List<Integer> list3 = new ArrayList<>();;
        for (List<Integer> list2:sets) {
            for (int i=0; i<list2.size(); i++) {
                for (int j=i; j<list2.size(); j++) {
                    list3 = new ArrayList<>();
                    for (int m=i; m<=j; m++) {
                        list3.add(list2.get(m));
                    }
                    set1.add(list3);
                }
            }
        }
        return set1.size();
    }

    public long appealSum(String s) {
        HashMap<Character, List<Integer>> map = new HashMap<>();
        int n = s.length();
        for (int i=0; i<n; i++) {
            List<Integer> list = map.getOrDefault(s.charAt(i), new ArrayList<>());
            list.add(i);
            map.put(s.charAt(i),list);
        }
        long res = 0;
        for (char c:map.keySet()) {
            int m = map.get(c).size();
            for (int i=0; i<m; i++) {
                int left = map.get(c).get(i)+1;
                int right = i==m-1 ? n-map.get(c).get(i) : map.get(c).get(i+1)-map.get(c).get(i);
                res += left*right;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Weekly291 pp = new Weekly291();
        String s1 = "7286877396768435886173873891455272614565655316543483788148827116697428274795272324627891875277173121";
        System.out.println(pp.removeDigit(s1, '7'));

        int[] nums = new int[] {10,2,17,7,20};
        System.out.println(pp.countDistinct(nums, 1, 10));
    }
}
