package io.trunkcat.fullplate.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class URL {
    static public String create(String path) {
        return path;
    }

    static public String create(String path, HashMap<String, String> searchParams) {
        if (searchParams != null && !searchParams.isEmpty()) {
            ArrayList<String> search = new ArrayList<>();
            for (Map.Entry<String, String> entry : searchParams.entrySet()) {
                search.add(entry.getKey() + "=" + entry.getValue());
            }
            String searchString = "?" + String.join("&", search);
            path += searchString;
        }
        return path;
    }
}
