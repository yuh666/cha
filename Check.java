

import java.util.*;
import java.util.regex.Pattern;

public class Check {


    private static Set<Integer> set = new HashSet<>();
    private static final Pattern pattern = Pattern.compile("，");
    PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o2.length() - o1.length();
        }
    });

    public void init(Vector<String> docList) {
        for (String s : docList) {
            String[] split = pattern.split(s);
            Collections.addAll(queue, split);
            int i = 0;
            StringBuilder sb = new StringBuilder();
            while (!queue.isEmpty() && i < 4) {
                sb.append(queue.poll());
                i++;
            }
            queue.clear();
            set.add(sb.toString().hashCode());
        }
    }


    public int check(char[] info, int infoLen) {
        String s = new String(info, 0, infoLen);
        String[] split = pattern.split(s);
        Collections.addAll(queue, split);
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty() && i < 4) {
            sb.append(queue.poll());
            i++;
        }
        queue.clear();
        return set.contains(sb.toString().hashCode()) ? 1 : 0;
    }


}

