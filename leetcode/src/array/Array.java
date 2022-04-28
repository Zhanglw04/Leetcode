package array;


public class Array {
    // 704
    public int search(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;
        if (nums.length==0)
            return -1;
        while (start<=end) {
            int mid = (start+end)/2;
            if (nums[mid]==target)
                return mid;
            else if (nums[mid]<target) {
                start = mid+1;
            }
            else {
                end = mid-1;
            }
        }
        return -1;
    }

    // 27 双指针法，同时从零开始
    public int removeElement(int[] nums, int val) {
        int n = nums.length;
        int left = 0;
        for (int right=0; right<n; right++) {
            if (nums[right] != val) {
                nums[left] = nums[right];
                left++;
            }
        }
        return left+1;
    }

    // 977 有序数组的平方, 双指针法，分别从两端开始
    public int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int left = 0;
        int right = n-1;
        int[] res = new int[n];
        for (int i=n-1; i>=0; i--) {
            if (nums[left]*nums[left]>nums[right]*nums[right]) {
                res[i] = nums[left]*nums[left];
                left++;
            }
            else {
                res[i] = nums[right]*nums[right];
                right--;
            }
        }
        return res;
    }

    // 209
    public int minSubArrayLen(int target, int[] nums) {
        int n = nums.length;
        int left = 0;
        int min = n+1;
        int sum = 0;
        for (int right=0; right<n; right++) {
            sum += nums[right];
            while (sum >= target && left <= right) {
                min = Math.min(min, right-left+1);
                sum -= nums[left];
                left++;
            }
        }
        if (min == n+1)
            return 0;
        return min;
    }

}
