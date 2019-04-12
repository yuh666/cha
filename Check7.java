
import java.util.*;
//基于bitset实现
public class Check7 {

    private class Node implements Comparable<Node> {
        BitSet bitSet;
        int count;


        Node() {
        }


        Node(BitSet bitSet, int count) {
            this.bitSet = bitSet;
            this.count = count;
        }

        @Override
        public int compareTo(Node o) {
            return this.count - o.count;
        }
    }

    private List<Node> list;


    public void init(Vector<String> docList) {
        list = new ArrayList<>(docList.size());
        for (String s : docList) {
            //如果有相似度为0.7的帖子 就不再加进去了
            int ratio = _check(s.toCharArray(), s.length(), 0.7D);
            if (ratio == 1) {
                continue;
            }
            //保存每个字符的unicode bitset占用内存小 而且比较快
            BitSet bitSet = new BitSet();
            for (int i = 0; i < s.length(); i++) {
                //自带去重功能
                bitSet.set(s.charAt(i));

            }
            //用Node是因为 bitSet.cardinality() 比较慢 后面就不用计算了
            list.add(new Node(bitSet, bitSet.cardinality()));
        }
        //字数从小到大排序
        Collections.sort(list);
    }


    public int check(char[] info, int infoLen) {
        return _check(info, infoLen, 0.6);
    }


    private int _check(char[] info, int infoLen, double ratio) {
        //字符去重
        HashSet<Character> set = new HashSet<>(infoLen);
        for (int i = 0; i < infoLen; i++) {
            set.add(info[i]);
        }
        //这是一个优化策略 比如标准文档有1000个字 那么待匹配的文档至少得有600个字才可能匹配
        //这里就是先知道了待匹配文档有多少个字 然后取出能匹配的最长的文档 比这个长度小的文档才能被匹配
        int v = (int) (set.size() / ratio) + 1;
        //查找第一个大于等于这个v的文档 参考 王争     二分查找的变种
        int gte = firstGte(list, v);
        if (gte == -1) {
            return 0;
        }
        //查找这个文档之前的所所有文档
        for (int j = 0; j < gte; j++) {
            Node node = list.get(j);
            int len = node.count;
            BitSet value = node.bitSet;
            int s = 0;
            for (Character character : set) {
                if (value.get(character)) {
                    //匹配上了就计算比例 到了ratio就成功
                    s++;
                    if ((double) s / len >= ratio) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }


    private int firstGte(List<Node> list, int k) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >> 1;
            Node node = list.get(mid);
            if (node.count < k) {
                low = mid + 1;
            } else if (mid == 0 || list.get(mid - 1).count < k) {
                return mid;
            } else {
                high = mid - 1;
            }
        }
        return list.size();
    }


}

