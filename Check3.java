
import java.util.*;

//基于反向索引bitset实现
public class Check3 {

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

    private Map<Character, List<Node>> map;


    public void init(Vector<String> docList) {
        map = new HashMap<>();
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
            Node node = new Node(bitSet, bitSet.cardinality());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                List<Node> list = map.get(c);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(node);
                map.put(c, list);
            }
        }
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
//        System.out.println(set);
        int v = (int) (set.size() / ratio) + 1;

        for (Character c : set) {
            List<Node> nodes = map.get(c);
            if(nodes ==null){
                continue;
            }
            for (Node node : nodes) {
                int len = node.count;
                if (len >= v) {
                    continue;
                }
                BitSet bitSet = node.bitSet;
                int s = 0;
                for (Character character : set) {
                    if (bitSet.get(character)) {
                        //匹配上了就计算比例 到了ratio就成功
                        s++;
                        if ((double) s / len >= ratio) {
                            return 1;
                        }
                    }
                }
            }
        }
        return 0;
    }


    private int firstGte(List<Node> list, int k) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
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

