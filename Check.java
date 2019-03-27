
import java.util.*;

public class Check {

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
            int ratio = _check(s.toCharArray(), s.length(), 0.7D);
            if (ratio == 1) {
                continue;
            }
            BitSet bitSet = new BitSet();
            for (int i = 0; i < s.length(); i++) {
                bitSet.set(s.charAt(i));

            }
            list.add(new Node(bitSet, bitSet.cardinality()));
        }
        Collections.sort(list);
    }


    public int check(char[] info, int infoLen) {
        return _check(info, infoLen, 0.6);
    }

    private int _check(char[] info, int infoLen, double ratio) {
        HashSet<Character> set = new HashSet<>(infoLen);
        for (int i = 0; i < infoLen; i++) {
            set.add(info[i]);
        }
        int v = (int) (set.size() / ratio) + 1;
        int gte = firstGte(list, v);
        if (gte == -1) {
            return 0;
        }
        for (int j = 0; j < gte; j++) {
            Node node = list.get(j);
            int len = node.count;
            BitSet value = node.bitSet;
            int s = 0;
            for (Character character : set) {
                if (value.get(character)) {
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

