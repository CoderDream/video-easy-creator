package com.coderdream.util.process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListSplitterStream {

    public static List<List<String>> splitList(List<String> originalList, int groupSize) {
        int size = originalList.size();
        return IntStream.range(0, size)
                .boxed()
                .collect(Collectors.groupingBy(i -> i / groupSize))
                .values()
                .stream()
                .map(indices -> indices.stream().map(originalList::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }


    public static void main(String[] args) {
         List<String> originalList = new ArrayList<>();
        for (int i = 0; i < 250; i++) {
            originalList.add("Item " + i);
        }

        int groupSize = 100;
        List<List<String>> splittedLists = ListSplitterStream.splitList(originalList, groupSize);

        for (int i = 0; i < splittedLists.size(); i++) {
            System.out.println("Group " + (i + 1) + ": " + splittedLists.get(i));
        }
    }
}
