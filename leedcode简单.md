#### [1.两数之和](https://leetcode-cn.com/problems/two-sum/)52.4%简单

给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。

你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。

你可以按任意顺序返回答案。

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[0];
    }
}
```

#### [9.回文数](https://leetcode-cn.com/problems/palindrome-number/) 57.7%简单

```java
class Solution {
    public boolean isPalindrome(int x) {
        String s = x+"";
        boolean flag = true;
        for(int i=0;i<s.length()/2;i++){
            if(s.charAt(i) != s.charAt(s.length()-1-i)) flag=false;
        }
        return flag;
    }
}
```

#### [13.罗马数字转整数](https://leetcode-cn.com/problems/roman-to-integer/)62.8%简单

```java
class Solution {
    public int romanToInt(String s) {
        char[] arr = s.toCharArray();
        int sum = 0;
        for(int i=arr.length-1;i>=0;i--){
            char c = arr[i];
            if(c=='V'){
                sum+=5;
                if(i>0 && arr[i-1]=='I'){
                    sum-=1;
                    i--;
                }
            }else if(c=='X'){
                sum+=10;
                if(i>0 && arr[i-1]=='I'){
                    sum-=1;
                    i--;
                }
            }else if(c=='L'){
                sum+=50;
                if(i>0 && arr[i-1]=='X'){
                    sum-=10;
                    i--;
                }
            }else if(c=='C'){
                sum+=100;
                if(i>0 && arr[i-1]=='X'){
                    sum-=10;
                    i--;
                }
            }else if(c=='D'){
                sum+=500;
                if(i>0 && arr[i-1]=='C'){
                    sum-=100;
                    i--;
                }
            }else if(c=='M'){
                sum+=1000;
                if(i>0 && arr[i-1]=='C'){
                    sum-=100;
                    i--;
                }
            }else if(c=='I'){
                sum+=1;
            }

        }

        return sum;
    }
}
```

#### [14.最长公共前缀](https://leetcode-cn.com/problems/longest-common-prefix/)41.8%简单

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if(strs.length==0) return "";
        if(strs[0].equals("")) return "";
        String prefix = "";
        int index = 0 ;
        out:
        for(int i=0;i<strs[0].length();i++){
            char c = strs[0].charAt(i);
            for(int j=1;j<strs.length;j++){
                if(i<strs[j].length()){
                    if(c==strs[j].charAt(i)){
                        index=i;
                    }else{
                        index=i-1;
                        break out;
                    }
                }else{
                    index=i-1;
                    break out;
                }
                
            }
        }
        prefix = strs[0].substring(0,index+1);

        return prefix;
    }
}
```

#### [20.有效的括号](https://leetcode-cn.com/problems/valid-parentheses/)44.6%简单

```java
class Solution {
    public boolean isValid(String s) {
        char[] arr = s.toCharArray();
        ArrayDeque<Character> opts = new ArrayDeque<Character>();
        for(int i=0;i<arr.length;i++){
            char c = arr[i];
            if(c=='(' || c=='[' || c=='{' ){
                opts.add(c);
            }else if(c==')'){
                if(opts.isEmpty()){
                   return false; 
                }
                char cl = opts.pollLast();
                System.out.println(cl);
                if(cl != '(') return false;
            }else if(c==']'){
                if(opts.isEmpty()){
                   return false; 
                }
                char cl = opts.pollLast();
                if(cl != '[') return false;
            }else if(c=='}'){
                if(opts.isEmpty()){
                   return false; 
                }
                char cl = opts.pollLast();
                if(cl != '{') return false;
            }
        }

        if(!opts.isEmpty()){
            return false;
        }

        return true;
    }
}
```

#### [21.合并两个有序链表](https://leetcode-cn.com/problems/merge-two-sorted-lists/)66.7%简单

将两个升序链表合并为一个新的 **升序** 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        if(list2==null){
            return list1;
        }else if(list1==null){
            return list2;
        }else{
            if(list1.val>list2.val){
                list2.next = mergeTwoLists(list2.next,list1);
                return list2;
            }else{
                list1.next = mergeTwoLists(list1.next,list2);
                return list1;
            }
        }
    }
}
```



#### [26.删除有序数组中的重复项](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array/)53.6%简单

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        int fast = 1, slow = 1;
        while(fast<n){
            if(nums[fast]!=nums[fast-1]){
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        return slow;
    }
}
```



#### [27.移除元素](https://leetcode-cn.com/problems/remove-element/)59.6%简单

给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素，并返回移除后数组的新长度。

不要使用额外的数组空间，你必须仅使用 O(1) 额外空间并 原地 修改输入数组。

元素的顺序可以改变。你不需要考虑数组中超出新长度后面的元素。

```java
class Solution {
    public int removeElement(int[] nums, int val) {
        int slow =0;
        int fast = 0;
        while(fast < nums.length){
            if(nums[fast]!=val){
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        return slow;
    }
}
```



#### [28.实现strStr()](https://leetcode-cn.com/problems/implement-strstr/)40.2%简单

```java

```



#### ---[53.最大子数组和](https://leetcode-cn.com/problems/maximum-subarray/)55.1%简单

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int pre=0;
        int max = nums[0];
        for(int x : nums){
            pre = Math.max(pre+x,x);
            max = Math.max(pre,max);
        }
        return max;
    }
}
```

#### [58.最后一个单词的长度](https://leetcode-cn.com/problems/length-of-last-word/)39.2%简单

```java

```

#### ---[66.加一](https://leetcode-cn.com/problems/plus-one/)46.2%简单

给定一个由 整数 组成的 非空 数组所表示的非负整数，在该数的基础上加一。
最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
你可以假设除了整数 0 之外，这个整数不会以零开头。

```java
class Solution {
    public int[] plusOne(int[] digits) {
        int jin = (digits[digits.length-1]+1)/10;
        digits[digits.length-1] = (digits[digits.length-1]+1)%10;
        if(digits.length>1){
            for(int i=digits.length-2;i>=0;i--){
                int val=digits[i]+jin;
                digits[i] = val%10;
                jin = val/10;
            }
        }
        if(jin>0){
            int[] arr = new int[digits.length+1];
            arr[0]=1;
            for(int i=0;i<digits.length;i++){
                arr[i+1]=digits[i];
            }
            return arr;
        }else{
            return digits;
        }
    }
}
```

#### ---[67.二进制求和](https://leetcode-cn.com/problems/add-binary/)54.0%简单

给你两个二进制字符串，返回它们的和（用二进制表示）。

输入为 非空 字符串且只包含数字 1 和 0。

示例 1:

输入: a = "11", b = "1"
输出: "100"
示例 2:

输入: a = "1010", b = "1011"
输出: "1

```java
class Solution {
    public String addBinary(String a, String b) {
        StringBuffer ans = new StringBuffer();

        int n = Math.max(a.length(), b.length()), carry = 0;
        for (int i = 0; i < n; ++i) {
            carry += i < a.length() ? (a.charAt(a.length() - 1 - i) - '0') : 0;
            carry += i < b.length() ? (b.charAt(b.length() - 1 - i) - '0') : 0;
            ans.append((char) (carry % 2 + '0'));
            carry /= 2;
        }

        if (carry > 0) {
            ans.append('1');
        }
        ans.reverse();

        return ans.toString();
    }
}
```

#### ---[69.x的平方根](https://leetcode-cn.com/problems/sqrtx/)39.0%简单

```java
class Solution {
    public int mySqrt(int x) {
        int left = 0, right = x, ans = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if ((long) mid * mid <= x) {
                ans = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return ans;
    }
}
```

#### [70.爬楼梯](https://leetcode-cn.com/problems/climbing-stairs/)53.5%简单

使用递归会超时

```java
class Solution {
    public int climbStairs(int n) {
        int p=0,q=0,r=1;
        for(int i=0;i<n;i++){
            p=q;
            q=r;
            r=p+q;
        }
        return r;
    }
}
```

#### [83.删除排序链表中的重复元素](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list/)53.7%简单

给定一个已排序的链表的头 `head` ， *删除所有重复的元素，使每个元素只出现一次* 。返回 *已排序的链表* 。

```java
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        if(head==null || head.next==null) return head;
        if(head.val == head.next.val){
            head=deleteDuplicates(head.next);
        }else{
            head.next=deleteDuplicates(head.next);
        }
        return head;
    }
}
```



#### [88.合并两个有序数组](https://leetcode-cn.com/problems/merge-sorted-array/)52.1%简单

```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        for (int i = 0; i < n; i++) {
            nums1[m + i] = nums2[i];
        }
        Arrays.sort(nums1);
    }
}
```

#### [94.二叉树的中序遍历](https://leetcode-cn.com/problems/binary-tree-inorder-traversal/)75.7%简单

```java
class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if(root!=null){
            list.addAll(inorderTraversal(root.left));
            list.add(root.val);
            list.addAll(inorderTraversal(root.right));
        }
        return list;
    }
}
```

#### [100.相同的树](https://leetcode-cn.com/problems/same-tree/)59.9%简单

```java
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if(p==null && q==null){
            return true;
        }else if(p==null || q==null){
            return false;
        }else if(p.val!=q.val){
            return false;
        }
        return isSameTree(p.left,q.left) && isSameTree(p.right,q.right);
    }
}
```

#### [101.对称二叉树](https://leetcode-cn.com/problems/symmetric-tree/)57.1%简单

```java
class Solution {
    public boolean isSymmetric(TreeNode root) {
        return check(root.left,root.right);
    }
    public boolean check(TreeNode left,TreeNode right){
        if(left==null && right==null){
            return true;
        }else if(left==null || right==null){
            return false;
        }
        return (left.val==right.val) && check(left.left,right.right) && check(left.right,right.left);
    }
}
```

#### [104.二叉树的最大深度](https://leetcode-cn.com/problems/maximum-depth-of-binary-tree/)76.9%简单

```java
class Solution {
    public int maxDepth(TreeNode root) {
        int deep=0;
        if(root!=null){
            deep++;
            return deep + Math.max(maxDepth(root.left),maxDepth(root.right));
        }
        return deep;
    }
}
```

#### ---[111.二叉树的最小深度](https://leetcode-cn.com/problems/minimum-depth-of-binary-tree/)49.6%简单

给定一个二叉树，找出其最小深度。

最小深度是从根节点到最近叶子节点的最短路径上的节点数量。

```java
class Solution {
    public int minDepth(TreeNode root) {
        if(root==null) return 0;
        if(root.left==null && root.right==null) return 1;
        int deep=Integer.MAX_VALUE;
        if(root.left!=null){
            deep = Math.min(deep,minDepth(root.left));
        }
        if(root.right!=null){
            deep = Math.min(deep,minDepth(root.right));
        }
        return deep+1;
    }
}
```



#### ---[112.路径总和](https://leetcode-cn.com/problems/path-sum/)53.0%简单

判断该树中是否存在 **根节点到叶子节点** 的路径，这条路径上所有节点值相加等于目标和 `targetSum` 

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) {
            return false;
        }
        Queue<TreeNode> queNode = new LinkedList<TreeNode>();
        Queue<Integer> queVal = new LinkedList<Integer>();
        queNode.offer(root);
        queVal.offer(root.val);
        while (!queNode.isEmpty()) {
            TreeNode now = queNode.poll();
            int temp = queVal.poll();
            if (now.left == null && now.right == null) {
                if (temp == sum) {
                    return true;
                }
                continue;
            }
            if (now.left != null) {
                queNode.offer(now.left);
                queVal.offer(now.left.val + temp);
            }
            if (now.right != null) {
                queNode.offer(now.right);
                queVal.offer(now.right.val + temp);
            }
        }
        return false;
    }
}
```

#### [118.杨辉三角](https://leetcode-cn.com/problems/pascals-triangle/)74.2%简单

```java
class Solution {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> list = new ArrayList<List<Integer>>();
        for(int i=0; i<numRows; i++){
            List<Integer> row = new ArrayList<Integer>();
            for(int j=0; j<=i; j++){
                if(j==0 || j==i){
                    row.add(1);
                }else{
                    List<Integer> last = list.get(list.size()-1);
                    int sum = last.get(j)+last.get(j-1);
                    row.add(sum);
                }
            }
            list.add(row);
        }
        return list;
    }
}
```

#### [121.买卖股票的最佳时机](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock/)57.5%简单

```java
public class Solution {
    public int maxProfit(int prices[]) {
        int minprice = Integer.MAX_VALUE;
        int maxprofit = 0;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < minprice) {
                minprice = prices[i];
            } else if (prices[i] - minprice > maxprofit) {
                maxprofit = prices[i] - minprice;
            }
        }
        return maxprofit;
    }
}
```

#### [125.验证回文串](https://leetcode-cn.com/problems/valid-palindrome/)47.1%简单

```java
class Solution {
    public boolean isPalindrome(String s) {
        StringBuffer sgood = new StringBuffer();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                sgood.append(Character.toLowerCase(ch));
            }
        }
        StringBuffer sgood_rev = new StringBuffer(sgood).reverse();
        return sgood.toString().equals(sgood_rev.toString());
    }
}
```

#### [136.只出现一次的数字](https://leetcode-cn.com/problems/single-number/)72.1%简单

给定一个**非空**整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。

```java
class Solution {
    public int singleNumber(int[] nums) {
        int single = 0;
        for (int num : nums) {
            single ^= num;
        }
        return single;
    }
}
```

#### [141.环形链表](https://leetcode-cn.com/problems/linked-list-cycle/)51.4%简单

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        Set<ListNode> seen = new HashSet<ListNode>();
        while (head != null) {
            if (!seen.add(head)) {
                return true;
            }
            head = head.next;
        }
        return false;
    }
}
```

#### [144.二叉树的前序遍历](https://leetcode-cn.com/problems/binary-tree-preorder-traversal/)70.7%简单

```java
class Solution {
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<Integer>();
        preorder(root, res);
        return res;
    }

    public void preorder(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        res.add(root.val);
        preorder(root.left, res);
        preorder(root.right, res);
    }
}
```

#### [145.二叉树的后序遍历](https://leetcode-cn.com/problems/binary-tree-postorder-traversal/)75.4%简单

```java
class Solution {
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<Integer>();
        preorder(root, res);
        return res;
    }

    public void preorder(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        
        preorder(root.left, res);
        preorder(root.right, res);
        res.add(root.val);
    }
}
```

#### [155.最小栈](https://leetcode-cn.com/problems/min-stack/)57.8%简单

```java
class MinStack {
    Deque<Integer> xStack;
    Deque<Integer> minStack;

    public MinStack() {
        xStack = new LinkedList<Integer>();
        minStack = new LinkedList<Integer>();
        minStack.push(Integer.MAX_VALUE);
    }
    
    public void push(int x) {
        xStack.push(x);
        minStack.push(Math.min(minStack.peek(), x));
    }
    
    public void pop() {
        xStack.pop();
        minStack.pop();
    }
    
    public int top() {
        return xStack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

#### [160.相交链表](https://leetcode-cn.com/problems/intersection-of-two-linked-lists/)62.2%简单

```java
public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        Set<ListNode> visited = new HashSet<ListNode>();
        ListNode temp = headA;
        while (temp != null) {
            visited.add(temp);
            temp = temp.next;
        }
        temp = headB;
        while (temp != null) {
            if (visited.contains(temp)) {
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }
}
```

#### [163.缺失的区间](https://leetcode-cn.com/problems/missing-ranges/)![plus](https://static.leetcode-cn.com/cn-mono-assets/production/assets/plus.31398c34.svg)33.9%简单

给定一个排序的整数数组 ***nums*** ，其中元素的范围在 **闭区间** **[\*lower, upper\*]** 当中，返回不包含在数组中的缺失区间。

输入: nums = [0, 1, 3, 50, 75], lower = 0 和 upper = 99,
输出: ["2", "4->49", "51->74", "76->99"]

```java
class Solution {
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
        // 答案数组
        List<String> ans = new ArrayList<>();
        
        int n = nums.length;
        if (n == 0) {
            // 记得处理数组为空的情况, 即添加low到high的每一个数字
            ans.add(helper(lower - 1, upper + 1));
            return ans;
        }
        
        // 1. 处理开头
        //    如果第一个数字不等于low的话
        //    这里记得要把left变成开区间, 不然helper
        //    里不会补上缺失的lower元素
        if (lower != nums[0]) {
            ans.add(helper(lower - 1, nums[0]));
        }
        
        // 2. 处理中间
        //    两两比较, 如果发现有空缺, 比如 5的下一个不是6
        //    则添加缺失元素
        for (int i = 0; i < n - 1; ++i) {
            if (nums[i] + 1 != nums[i + 1]) {
                ans.add(helper(nums[i], nums[i + 1]));
            }
        }
        
        // 3. 处理结尾
        //    如果最后一个数字不等于high的话
        //    同理, 记得把high变为开区间 即 high + 1
        if (upper != nums[n - 1]) {
            ans.add(helper(nums[n - 1], upper + 1)); 
        }
        
        return ans;
    }

    private String helper(int left, int right) {
        StringBuilder sb = new StringBuilder();
        
        // 如果中间只缺少一个元素, 则只添加那一个元素
        if (left + 2 == right) {
            sb.append(left + 1);
        } else {
        // 如果缺失了一段数字, 则记得添加 ->
            sb.append(left + 1).append("->").append(right - 1);
        }
        
        return sb.toString();
    }
}

```

#### [168.Excel表列名称](https://leetcode-cn.com/problems/excel-sheet-column-title/)43.4%简单

```java
class Solution {
    public String convertToTitle(int columnNumber) {
        StringBuffer sb = new StringBuffer();
        while (columnNumber != 0) {
            columnNumber--;
            sb.append((char)(columnNumber % 26 + 'A'));
            columnNumber /= 26;
        }
        return sb.reverse().toString();
    }
}
```

#### [169.多数元素](https://leetcode-cn.com/problems/majority-element/)66.6%简单

给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。

你可以假设数组是非空的，并且给定的数组总是存在多数元素。

 

示例 1：

输入：[3,2,3]
输出：3
示例 2：

输入：[2,2,1,1,1,2,2]
输出：2

```java
class Solution {
    public int majorityElement(int[] nums) {
        int count = 0;
        Integer candidate = null;

        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }
}
```

#### [171.Excel表列序号](https://leetcode-cn.com/problems/excel-sheet-column-number/)71.7%简单

```java
class Solution {
    public int titleToNumber(String columnTitle) {
        int number = 0;
        int multiple = 1;
        for (int i = columnTitle.length() - 1; i >= 0; i--) {
            int k = columnTitle.charAt(i) - 'A' + 1;
            number += k * multiple;
            multiple *= 26;
        }
        return number;
    }
}
```

#### [175.组合两个表](https://leetcode-cn.com/problems/combine-two-tables/)73.5%简单

```java
# Write your MySQL query statement below
select FirstName, LastName, City, State
from Person left join Address
on Person.PersonId=Address.PersonId
```

#### [181.超过经理收入的员工](https://leetcode-cn.com/problems/employees-earning-more-than-their-managers/)69.5%简单

```java
select a.name as Employee from Employee a left join Employee b on a.managerId=b.id
where a.managerId is not null
and a.salary > b.salary
```

#### [182.查找重复的电子邮箱](https://leetcode-cn.com/problems/duplicate-emails/)79.4%简单

```java
# Write your MySQL query statement below
select Email from Person a group by a.Email having(count(1))>1
```

#### [191.位1的个数](https://leetcode-cn.com/problems/number-of-1-bits/)75.3%简单

```java
public class Solution {
    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        int count=0;
        while(n!=0){
            count++;
            n=n & (n-1);
        }
        return count;
    }
}
```

#### [193.有效电话号码](https://leetcode-cn.com/problems/valid-phone-numbers/)32.9%简单

```bash
grep -P '^([0-9]{3}-|\([0-9]{3}\) )[0-9]{3}-[0-9]{4}$' file.txt
```

#### [195.第十行](https://leetcode-cn.com/problems/tenth-line/)43.9%简单

```java
sed -n "10p" file.txt
```

#### [196.删除重复的电子邮箱](https://leetcode-cn.com/problems/delete-duplicate-emails/)66.4%简单

```java
DELETE p1 FROM Person p1,
    Person p2
WHERE
    p1.Email = p2.Email AND p1.Id > p2.Id
```

#### [202.快乐数](https://leetcode-cn.com/problems/happy-number/)62.4%简单

```java

```

#### [203.移除链表元素](https://leetcode-cn.com/problems/remove-linked-list-elements/)52.8%简单

```java

```

#### [205.同构字符串](https://leetcode-cn.com/problems/isomorphic-strings/)49.9%简单

```java

```

#### [206.反转链表](https://leetcode-cn.com/problems/reverse-linked-list/)72.7%简单

```java

```

#### [219.存在重复元素II](https://leetcode-cn.com/problems/contains-duplicate-ii/)44.5%简单

```java

```

#### [225.用队列实现栈](https://leetcode-cn.com/problems/implement-stack-using-queues/)67.6%简单

```java

```

#### [226.翻转二叉树](https://leetcode-cn.com/problems/invert-binary-tree/)79.0%简单

```java

```



#### [228.汇总区间](https://leetcode-cn.com/problems/summary-ranges/)57.4%简单

```java

```

#### [231.2的幂](https://leetcode-cn.com/problems/power-of-two/)50.5%简单

```java

```

#### [234.回文链表](https://leetcode-cn.com/problems/palindrome-linked-list/)50.8%简单

```java

```

#### [235.二叉搜索树的最近公共祖先](https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-search-tree/)67.0%简单

```java

```

#### [237.删除链表中的节点](https://leetcode-cn.com/problems/delete-node-in-a-linked-list/)85.8%简单

```java

```

#### [242.有效的字母异位词](https://leetcode-cn.com/problems/valid-anagram/)65.0%简单

```java

```

#### [257.二叉树的所有路径](https://leetcode-cn.com/problems/binary-tree-paths/)69.1%简单

```java

```

#### [258.各位相加](https://leetcode-cn.com/problems/add-digits/)69.3%简单

```java

```

#### [263.丑数](https://leetcode-cn.com/problems/ugly-number/)51.4%简单

```java

```

#### [268.丢失的数字](https://leetcode-cn.com/problems/missing-number/)65.4%简单

```java

```

#### [278.第一个错误的版本](https://leetcode-cn.com/problems/first-bad-version/)45.1%简单

```java

```

#### [283.移动零](https://leetcode-cn.com/problems/move-zeroes/)64.0%简单

```java

```

#### [290.单词规律](https://leetcode-cn.com/problems/word-pattern/)45.5%简单

```java

```

#### [292.Nim游戏](https://leetcode-cn.com/problems/nim-game/)70.8%简单

```java

```

#### [303.区域和检索-数组不可变](https://leetcode-cn.com/problems/range-sum-query-immutable/)73.7%简单

```java

```

#### [326.3的幂](https://leetcode-cn.com/problems/power-of-three/)50.6%简单

```java

```

#### [338.比特位计数](https://leetcode-cn.com/problems/counting-bits/)78.7%简单

```java

```

#### [344.反转字符串](https://leetcode-cn.com/problems/reverse-string/)78.0%简单

```java

```

#### [349.两个数组的交集](https://leetcode-cn.com/problems/intersection-of-two-arrays/)73.9%简单

```java

```

#### [350.两个数组的交集II](https://leetcode-cn.com/problems/intersection-of-two-arrays-ii/)55.6%简单

```java

```

#### [367.有效的完全平方数](https://leetcode-cn.com/problems/valid-perfect-square/)44.8%简单

```java

```

#### [383.赎金信](https://leetcode-cn.com/problems/ransom-note/)64.5%简单

```java

```

#### [387.字符串中的第一个唯一字符](https://leetcode-cn.com/problems/first-unique-character-in-a-string/)54.3%简单

```java

```

#### [389.找不同](https://leetcode-cn.com/problems/find-the-difference/)68.9%简单

```java

```

#### [392.判断子序列](https://leetcode-cn.com/problems/is-subsequence/)51.8%简单

```java

```

#### [409.最长回文串](https://leetcode-cn.com/problems/longest-palindrome/)55.7%简单

```java

```

#### [414.第三大的数](https://leetcode-cn.com/problems/third-maximum-number/)39.4%简单

```java

```

#### [415.字符串相加](https://leetcode-cn.com/problems/add-strings/)54.4%简单

```java

```

#### [434.字符串中的单词数](https://leetcode-cn.com/problems/number-of-segments-in-a-string/)39.8%简单

```java

```

#### [448.找到所有数组中消失的数字](https://leetcode-cn.com/problems/find-all-numbers-disappeared-in-an-array/)65.2%简单

```java

```

#### [455.分发饼干](https://leetcode-cn.com/problems/assign-cookies/)57.5%简单

```java

```

#### [459.重复的子字符串](https://leetcode-cn.com/problems/repeated-substring-pattern/)51.0%简单

```java

```

#### [461.汉明距离](https://leetcode-cn.com/problems/hamming-distance/)81.4%简单

```java

```

#### [463.岛屿的周长](https://leetcode-cn.com/problems/island-perimeter/)70.4%简单

```java

```

#### [476.数字的补数](https://leetcode-cn.com/problems/number-complement/)71.2%简单

```java

```

#### [482.密钥格式化](https://leetcode-cn.com/problems/license-key-formatting/)47.2%简单

```java

```

#### [504.七进制数](https://leetcode-cn.com/problems/base-7/)50.3%简单

```java

```

#### [506.相对名次](https://leetcode-cn.com/problems/relative-ranks/)65.4%简单

```java

```

#### [509.斐波那契数](https://leetcode-cn.com/problems/fibonacci-number/)67.0%简单

```java

```

#### [521.最长特殊序列Ⅰ](https://leetcode-cn.com/problems/longest-uncommon-subsequence-i/)71.4%简单

```java

```

#### [543.二叉树的直径](https://leetcode-cn.com/problems/diameter-of-binary-tree/)56.1%简单

```java

```

#### [557.反转字符串中的单词III](https://leetcode-cn.com/problems/reverse-words-in-a-string-iii/)74.3%简单

```java

```

#### [572.另一棵树的子树](https://leetcode-cn.com/problems/subtree-of-another-tree/)47.5%简单

```java

```

#### [595.大的国家](https://leetcode-cn.com/problems/big-countries/)78.0%简单

```java

```

#### [605.种花问题](https://leetcode-cn.com/problems/can-place-flowers/)33.2%简单

```java

```

#### [617.合并二叉树](https://leetcode-cn.com/problems/merge-two-binary-trees/)78.8%简单

```java

```

#### [628.三个数的最大乘积](https://leetcode-cn.com/problems/maximum-product-of-three-numbers/)52.5%简单

```java

```

#### [637.二叉树的层平均值](https://leetcode-cn.com/problems/average-of-levels-in-binary-tree/)69.3%简单

```java

```

#### [680.验证回文字符串Ⅱ](https://leetcode-cn.com/problems/valid-palindrome-ii/)40.1%简单

```java

```

#### [697.数组的度](https://leetcode-cn.com/problems/degree-of-an-array/)60.3%简单

```java

```



#### [703.数据流中的第K大元素](https://leetcode-cn.com/problems/kth-largest-element-in-a-stream/)51.6%简单

```java

```

#### [705.设计哈希集合](https://leetcode-cn.com/problems/design-hashset/)64.0%简单

```java

```

#### [706.设计哈希映射](https://leetcode-cn.com/problems/design-hashmap/)64.1%简单

```java

```

#### [746.使用最小花费爬楼梯](https://leetcode-cn.com/problems/min-cost-climbing-stairs/)60.6%简单

```java

```

#### [747.至少是其他数字两倍的最大数](https://leetcode-cn.com/problems/largest-number-at-least-twice-of-others/)45.8%简单

```java

```

#### [771.宝石与石头](https://leetcode-cn.com/problems/jewels-and-stones/)85.2%简单

```java

```

#### [821.字符的最短距离](https://leetcode-cn.com/problems/shortest-distance-to-a-character/)69.3%简单

```java

```

#### [832.翻转图像](https://leetcode-cn.com/problems/flipping-an-image/)79.5%简单

```java

```

#### [836.矩形重叠](https://leetcode-cn.com/problems/rectangle-overlap/)48.3%简单

```java

```

#### [860.柠檬水找零](https://leetcode-cn.com/problems/lemonade-change/)58.6%简单

```java

```

#### [867.转置矩阵](https://leetcode-cn.com/problems/transpose-matrix/)66.9%简单

```java

```

#### [876.链表的中间结点](https://leetcode-cn.com/problems/middle-of-the-linked-list/)70.7%简单

```java

```

#### [917.仅仅反转字母](https://leetcode-cn.com/problems/reverse-only-letters/)60.3%简单

```java

```

#### [961.在长度2N的数组中找出重复N次的元素](https://leetcode-cn.com/problems/n-repeated-element-in-size-2n-array/)67.7%简单

```java

```

#### [976.三角形的最大周长](https://leetcode-cn.com/problems/largest-perimeter-triangle/)59.5%简单

```java

```

#### [977.有序数组的平方](https://leetcode-cn.com/problems/squares-of-a-sorted-array/)69.5%简单

```java

```

#### [997.找到小镇的法官](https://leetcode-cn.com/problems/find-the-town-judge/)53.4%简单

```java

```

#### [1013.将数组分成和相等的三个部分](https://leetcode-cn.com/problems/partition-array-into-three-parts-with-equal-sum/)39.3%简单

```java

```

#### [1018.可被5整除的二进制前缀](https://leetcode-cn.com/problems/binary-prefix-divisible-by-5/)51.5%简单

```java

```

#### [1025.除数博弈](https://leetcode-cn.com/problems/divisor-game/)70.8%简单

```java

```

#### [1047.删除字符串中的所有相邻重复项](https://leetcode-cn.com/problems/remove-all-adjacent-duplicates-in-string/)72.6%简单

```java

```

#### [1071.字符串的最大公因子](https://leetcode-cn.com/problems/greatest-common-divisor-of-strings/)58.7%简单

```java

```

#### [1103.分糖果II](https://leetcode-cn.com/problems/distribute-candies-to-people/)63.7%简单

```java

```

#### [1108.IP地址无效化](https://leetcode-cn.com/problems/defanging-an-ip-address/)83.8%简单

```java

```

#### [1114.按序打印](https://leetcode-cn.com/problems/print-in-order/)65.3%简单

```java

```

#### [1122.数组的相对排序](https://leetcode-cn.com/problems/relative-sort-array/)70.7%简单

```java

```

#### [1160.拼写单词](https://leetcode-cn.com/problems/find-words-that-can-be-formed-by-characters/)68.5%简单

```java

```

#### [1165.单行键盘](https://leetcode-cn.com/problems/single-row-keyboard/)![plus](https://static.leetcode-cn.com/cn-mono-assets/production/assets/plus.31398c34.svg)83.4%简单

```java

```

#### [1221.分割平衡字符串](https://leetcode-cn.com/problems/split-a-string-in-balanced-strings/)84.4%简单

```java

```

#### [1232.缀点成线](https://leetcode-cn.com/problems/check-if-it-is-a-straight-line/)46.7%简单

```java

```

#### [1299.将每个元素替换为右侧最大元素](https://leetcode-cn.com/problems/replace-elements-with-greatest-element-on-right-side/)78.8%简单

```java

```

#### [1313.解压缩编码列表](https://leetcode-cn.com/problems/decompress-run-length-encoded-list/)83.2%简单

```java

```

#### [1360.日期之间隔几天](https://leetcode-cn.com/problems/number-of-days-between-two-dates/)51.0%简单

```java

```

#### [1365.有多少小于当前数字的数字](https://leetcode-cn.com/problems/how-many-numbers-are-smaller-than-the-current-number/)82.4%简单

```java

```

#### [1422.分割字符串的最大得分](https://leetcode-cn.com/problems/maximum-score-after-splitting-a-string/)54.1%简单

```java

```

#### [1436.旅行终点站](https://leetcode-cn.com/problems/destination-city/)82.2%简单

```java

```

#### [1450.在既定时间做作业的学生人数](https://leetcode-cn.com/problems/number-of-students-doing-homework-at-a-given-time/)80.4%简单

```java

```

#### [1480.一维数组的动态和](https://leetcode-cn.com/problems/running-sum-of-1d-array/)86.9%简单

```java

```

#### [1518.换酒问题](https://leetcode-cn.com/problems/water-bottles/)70.3%简单

```java

```

#### [1572.矩阵对角线元素的和](https://leetcode-cn.com/problems/matrix-diagonal-sum/)80.1%简单

```java

```

#### [1603.设计停车系统](https://leetcode-cn.com/problems/design-parking-system/)84.5%简单

```java

```

#### [剑指Offer03.数组中重复的数字](https://leetcode-cn.com/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof/)67.9%简单

```java

```

#### [剑指Offer05.替换空格](https://leetcode-cn.com/problems/ti-huan-kong-ge-lcof/)76.1%简单

```java

```

#### [剑指Offer09.用两个栈实现队列](https://leetcode-cn.com/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/)71.2%简单

```java

```

#### [剑指Offer10-I.斐波那契数列](https://leetcode-cn.com/problems/fei-bo-na-qi-shu-lie-lcof/)36.2%简单

```java

```

#### [剑指Offer10-II.青蛙跳台阶问题](https://leetcode-cn.com/problems/qing-wa-tiao-tai-jie-wen-ti-lcof/)45.1%简单

```java

```

#### [剑指Offer11.旋转数组的最小数字](https://leetcode-cn.com/problems/xuan-zhuan-shu-zu-de-zui-xiao-shu-zi-lcof/)49.2%简单

```java

```

#### [剑指Offer18.删除链表的节点](https://leetcode-cn.com/problems/shan-chu-lian-biao-de-jie-dian-lcof/)60.0%简单

```java

```

#### [剑指Offer21.调整数组顺序使奇数位于偶数前面](https://leetcode-cn.com/problems/diao-zheng-shu-zu-shun-xu-shi-qi-shu-wei-yu-ou-shu-qian-mian-lcof/)64.6%简单

```java

```

#### [剑指Offer22.链表中倒数第k个节点](https://leetcode-cn.com/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/)80.1%简单

```java

```

#### [剑指Offer24.反转链表](https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/)74.3%简单

```java

```

#### [剑指Offer25.合并两个排序的链表](https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/)72.8%简单

```java

```

#### [剑指Offer27.二叉树的镜像](https://leetcode-cn.com/problems/er-cha-shu-de-jing-xiang-lcof/)79.4%简单

```java

```

#### [剑指Offer29.顺时针打印矩阵](https://leetcode-cn.com/problems/shun-shi-zhen-da-yin-ju-zhen-lcof/)43.9%简单

```java

```

#### [剑指Offer30.包含min函数的栈](https://leetcode-cn.com/problems/bao-han-minhan-shu-de-zhan-lcof/)55.6%简单

```java

```

#### [剑指Offer40.最小的k个数](https://leetcode-cn.com/problems/zui-xiao-de-kge-shu-lcof/)57.3%简单

```java

```

#### [剑指Offer42.连续子数组的最大和](https://leetcode-cn.com/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/)60.7%简单

```java

```

#### [剑指Offer50.第一个只出现一次的字符](https://leetcode-cn.com/problems/di-yi-ge-zhi-chu-xian-yi-ci-de-zi-fu-lcof/)62.1%简单

```java

```

#### [剑指Offer53-II.0～n-1中缺失的数字](https://leetcode-cn.com/problems/que-shi-de-shu-zi-lcof/)44.9%简单

```java

```

#### [剑指Offer55-I.二叉树的深度](https://leetcode-cn.com/problems/er-cha-shu-de-shen-du-lcof/)79.1%简单

```java

```

#### [剑指Offer57.和为s的两个数字](https://leetcode-cn.com/problems/he-wei-sde-liang-ge-shu-zi-lcof/)67.4%简单

```java

```

#### [剑指Offer57-II.和为s的连续正数序列](https://leetcode-cn.com/problems/he-wei-sde-lian-xu-zheng-shu-xu-lie-lcof/)71.0%简单

```java

```

#### [剑指Offer58-I.翻转单词顺序](https://leetcode-cn.com/problems/fan-zhuan-dan-ci-shun-xu-lcof/)44.4%简单

```java

```

#### [剑指Offer58-II.左旋转字符串](https://leetcode-cn.com/problems/zuo-xuan-zhuan-zi-fu-chuan-lcof/)86.0%简单

```java

```

#### [剑指Offer62.圆圈中最后剩下的数字](https://leetcode-cn.com/problems/yuan-quan-zhong-zui-hou-sheng-xia-de-shu-zi-lcof/)65.8%简单

```java

```

#### [面试题01.03.URL化](https://leetcode-cn.com/problems/string-to-url-lcci/)57.7%简单

```java

```

#### [面试题01.06.字符串压缩](https://leetcode-cn.com/problems/compress-string-lcci/)46.9%简单

```java

```

#### [面试题02.01.移除重复节点](https://leetcode-cn.com/problems/remove-duplicate-node-lcci/)67.6%简单

```java

```

#### [面试题02.02.返回倒数第k个节点](https://leetcode-cn.com/problems/kth-node-from-end-of-list-lcci/)78.0%简单

```java

```

#### [面试题02.03.删除中间节点](https://leetcode-cn.com/problems/delete-middle-node-lcci/)85.6%简单

```java

```

#### [面试题02.06.回文链表](https://leetcode-cn.com/problems/palindrome-linked-list-lcci/)48.5%简单

```java

```

#### [面试题03.02.栈的最小值](https://leetcode-cn.com/problems/min-stack-lcci/)61.5%简单

```java
class MinStack {
    Deque<Integer> xStack;
    Deque<Integer> minStack;

    public MinStack() {
        xStack = new LinkedList<Integer>();
        minStack = new LinkedList<Integer>();
        minStack.push(Integer.MAX_VALUE);
    }
    
    public void push(int x) {
        xStack.push(x);
        minStack.push(Math.min(minStack.peek(), x));
    }
    
    public void pop() {
        xStack.pop();
        minStack.pop();
    }
    
    public int top() {
        return xStack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

#### [面试题08.03.魔术索引](https://leetcode-cn.com/problems/magic-index-lcci/)67.5%简单

```java
class Solution {
    public int findMagicIndex(int[] nums) {
        for(int i=0;i<nums.length;i++){
            if(nums[i] == i){
                return i;
            }
        }
        return -1;
    }
}

class Solution {
    public int findMagicIndex(int[] nums) {
        return getAnswer(nums, 0, nums.length - 1);
    }

    public int getAnswer(int[] nums, int left, int right) {
        if (left > right) {
            return -1;
        }
        int mid = (right - left) / 2 + left;
        int leftAnswer = getAnswer(nums, left, mid - 1);
        if (leftAnswer != -1) {
            return leftAnswer;
        } else if (nums[mid] == mid) {
            return mid;
        }
        return getAnswer(nums, mid + 1, right);
    }
}
```

#### [面试题10.01.合并排序的数组](https://leetcode-cn.com/problems/sorted-merge-lcci/)55.8%简单

```java
class Solution {
    public void merge(int[] A, int m, int[] B, int n) {
        for (int i = 0; i != n; ++i) {
            A[m + i] = B[i];
        }
        Arrays.sort(A);
    }
}
```

#### [面试题16.07.最大数值](https://leetcode-cn.com/problems/maximum-lcci/)73.0%简单

```java
class Solution {
    public int maximum(int a, int b) {
        // 先考虑没有溢出时的情况，计算 b - a 的最高位，依照题目所给提示 k = 1 时 a > b，即 b - a 为负
        int k = b - a >>> 31;
        // 再考虑 a b 异号的情况，此时无脑选是正号的数字
        int aSign = a >>> 31, bSign = b >>> 31;
        // diff = 0 时同号，diff = 1 时异号
        int diff = aSign ^ bSign;
        // 在异号，即 diff = 1 时，使之前算出的 k 无效，只考虑两个数字的正负关系
        k = k & (diff ^ 1) | bSign & diff;
        return a * k + b * (k ^ 1);
    }
}
```

#### [面试题17.16.按摩师](https://leetcode-cn.com/problems/the-masseuse-lcci/)51.6%简单

一个有名的按摩师会收到源源不断的预约请求，每个预约都可以选择接或不接。在每次预约服务之间要有休息时间，因此她不能接受相邻的预约。给定一个预约请求序列，替按摩师找到最优的预约集合（总预约时间最长），返回总的分钟数。

```java
class Solution {
    public int massage(int[] nums) {
        int first=0;
        int second=0;
        for(int i:nums){
            int temp=second;
            second=Math.max(second,first+i);
            first=temp;
        }
        return second;
    }
}
class Solution {
    public int massage(int[] nums) {
        int n = nums.length;
        if (n == 0) {
            return 0;
        }
        int dp0 = 0, dp1 = nums[0];

        for (int i = 1; i < n; ++i){
            int tdp0 = Math.max(dp0, dp1); // 计算 dp[i][0]
            int tdp1 = dp0 + nums[i]; // 计算 dp[i][1]

            dp0 = tdp0; // 用 dp[i][0] 更新 dp_0
            dp1 = tdp1; // 用 dp[i][1] 更新 dp_1
        }
        return Math.max(dp0, dp1);
    }
}
```





