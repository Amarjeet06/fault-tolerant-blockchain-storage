import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private List<String> transactions;
    private String root;

    public MerkleTree(List<String> transactions) {
        this.transactions = transactions;
        this.root = buildTree(transactions);
    }

    private String buildTree(List<String> transactions) {
        List<String> currentLayer = new ArrayList<>(transactions);
        
        while (currentLayer.size() > 1) {
            List<String> nextLayer = new ArrayList<>();
            for (int i = 0; i < currentLayer.size(); i += 2) {
                String left = currentLayer.get(i);
                String right = (i + 1 < currentLayer.size()) ? currentLayer.get(i + 1) : left;
                nextLayer.add(StringUtil.applySha256(left + right));
            }
            currentLayer = nextLayer;
        }
        
        return currentLayer.get(0);
    }

    public String getRootHash() {
        return root;
    }

    public static class MerkleProof {
        public final List<String> hashes;
        public final List<Boolean> isLeft;

        public MerkleProof(List<String> hashes, List<Boolean> isLeft) {
            this.hashes = hashes;
            this.isLeft = isLeft;
        }
    }

    public static boolean verifyProof(MerkleProof proof, String leaf, String root) {
        String computedHash = leaf;
        for (int i = 0; i < proof.hashes.size(); i++) {
            if (proof.isLeft.get(i)) {
                computedHash = StringUtil.applySha256(proof.hashes.get(i) + computedHash);
            } else {
                computedHash = StringUtil.applySha256(computedHash + proof.hashes.get(i));
            }
        }
        return computedHash.equals(root);
    }
}