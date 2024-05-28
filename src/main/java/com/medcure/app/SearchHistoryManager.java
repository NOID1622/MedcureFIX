package com.medcure.app;

import java.util.LinkedList;
import java.util.List;

public class SearchHistoryManager {
    private static final int MAX_HISTORY_SIZE = 5;
    private LinkedList<String> searchHistory = new LinkedList<>();

    public void addSearchQuery(String query) {
        if (searchHistory.size() >= MAX_HISTORY_SIZE) {
            searchHistory.removeFirst();
        }
        searchHistory.addLast(query);
    }

    public List<String> getRecentSearches() {
        return new LinkedList<>(searchHistory);
    }
}
