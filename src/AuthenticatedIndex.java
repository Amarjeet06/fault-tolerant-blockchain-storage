package com.backblaze.erasure;

import java.util.*;

public class AuthenticatedIndex {

    private final TreeMap<String, TreeMap<Integer, BlockVersion>> versionedIndex = new TreeMap<>();

    public void addVersion(String key, int year, String value) {
        TreeMap<Integer, BlockVersion> versions = versionedIndex.getOrDefault(key, new TreeMap<>());
        String prevHash = versions.isEmpty() ? "GENESIS" : versions.lastEntry().getValue().hash;
        BlockVersion version = new BlockVersion(value, prevHash);
        versions.put(year, version);
        versionedIndex.put(key, versions);
    }

    public Map<String, TreeMap<Integer, BlockVersion>> getRange(String startKey, String endKey, int startYear, int endYear) {
        Map<String, TreeMap<Integer, BlockVersion>> result = new TreeMap<>();
        for (String key : versionedIndex.subMap(startKey, true, endKey, true).keySet()) {
            TreeMap<Integer, BlockVersion> versions = versionedIndex.get(key);
            TreeMap<Integer, BlockVersion> range = new TreeMap<>(versions.subMap(startYear, true, endYear, true));
            if (!range.isEmpty()) {
                result.put(key, range);
            }
        }
        return result;
    }

    public void printIndex() {
        for (Map.Entry<String, TreeMap<Integer, BlockVersion>> entry : versionedIndex.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            for (Map.Entry<Integer, BlockVersion> ver : entry.getValue().entrySet()) {
                System.out.println("  Year: " + ver.getKey() + ", Value: " + ver.getValue().data + ", Hash: " + ver.getValue().hash);
            }
        }
    }

    public boolean verifyVersion(BlockVersion version) {
        return version.isValid();
    }
}
