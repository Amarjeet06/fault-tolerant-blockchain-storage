import java.util.*;
import java.util.stream.*;

public class Blockchain {
    public List<Block> chain = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();
    private Map<String, String> queryCache = new HashMap<>();
    public Set<Integer> failedNodes = new HashSet<>();

    public Blockchain() {
        for (int i = 0; i < 6; i++) {
            nodes.add(new Node(i));
        }
    }

    public void addBlock(String data) {
        Block block = new Block(chain.size(), data, getLatestHash());
        chain.add(block);
        distributeFragments(block);
    }

    private void distributeFragments(Block block) {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).fragments.add(block.fragments[i]);
        }
    }

    public List<String> queryRangeByYear(int startYear, int endYear) {
        String cacheKey = startYear + "-" + endYear;
        if (queryCache.containsKey(cacheKey)) {
            return Arrays.asList(queryCache.get(cacheKey).split("\n"));
        }

        List<String> results = new ArrayList<>();
        for (Block block : chain) {
            int year = extractYearFromData(block.data);
            if (year >= startYear && year <= endYear) {
                try {
                    byte[][] fragments = new byte[nodes.size()][];
                    int fragmentsAvailable = 0;
                    
                    for (int j = 0; j < nodes.size(); j++) {
                        if (!failedNodes.contains(j)) {
                            fragments[j] = nodes.get(j).fragments.get(block.index);
                            fragmentsAvailable++;
                        }
                    }
                    
                    if (fragmentsAvailable >= 4) {
                        results.add(ReedSolomonHelper.decode(fragments));
                    } else {
                        results.add("[Insufficient fragments for block " + block.index + "]");
                    }
                } catch (Exception e) {
                    results.add("[Error decoding block " + block.index + ": " + e.getMessage() + "]");
                }
            }
        }
        
        queryCache.put(cacheKey, String.join("\n", results));
        return results;
    }

    private int extractYearFromData(String data) {
        try {
            return Integer.parseInt(data.split(",")[0].trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private String getLatestHash() {
        return chain.isEmpty() ? "0" : chain.get(chain.size() - 1).hash;
    }

    public void simulateNodeFailure(int nodeId) {
        failedNodes.add(nodeId);
        System.out.println("Simulated failure of Node " + nodeId);
    }

    public boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i-1);
            
            if (!current.hash.equals(current.calculateHash())) {
                return false;
            }
            
            if (!current.previousHash.equals(previous.hash)) {
                return false;
            }
        }
        return true;
    }
}