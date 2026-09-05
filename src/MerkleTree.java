import java.util.ArrayList;
import java.util.List;

/**
 * Binary Merkle tree over a list of transaction/record strings.
 *
 * NOTE: the original version of this class only stored the final root hash,
 * so verifyProof() had no way to be exercised - there was no method to
 * actually produce a MerkleProof for a given leaf. This version keeps every
 * intermediate layer so getProof(index) can build a real inclusion proof,
 * verified end-to-end in MerkleTreeTest.
 */
public class MerkleTree {
    private final List<String> transactions;
    private final List<List<String>> layers = new ArrayList<>();
    private final String root;

    public MerkleTree(List<String> transactions) {
        this.transactions = new ArrayList<>(transactions);
        this.root = buildTree(this.transactions);
    }

    private String buildTree(List<String> leaves) {
        List<String> currentLayer = new ArrayList<>(leaves);
        layers.add(currentLayer);

        while (currentLayer.size() > 1) {
            List<String> nextLayer = new ArrayList<>();
            for (int i = 0; i < currentLayer.size(); i += 2) {
                String left = currentLayer.get(i);
                String right = (i + 1 < currentLayer.size()) ? currentLayer.get(i + 1) : left;
                nextLayer.add(StringUtil.applySha256(left + right));
            }
            currentLayer = nextLayer;
            layers.add(currentLayer);
        }

        return currentLayer.get(0);
    }

    public String getRootHash() {
        return root;
    }

    /** Builds an inclusion proof for the leaf at {@code index}. */
    public MerkleProof getProof(int index) {
        List<String> hashes = new ArrayList<>();
        List<Boolean> isLeft = new ArrayList<>();

        int idx = index;
        for (int layer = 0; layer < layers.size() - 1; layer++) {
            List<String> current = layers.get(layer);
            boolean isRightNode = (idx % 2 == 1);
            int siblingIdx = isRightNode ? idx - 1 : idx + 1;
            String sibling = siblingIdx < current.size() ? current.get(siblingIdx) : current.get(idx);

            hashes.add(sibling);
            // if this node is the right child, the sibling goes on the left
            isLeft.add(isRightNode);

            idx = idx / 2;
        }
        return new MerkleProof(hashes, isLeft);
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
