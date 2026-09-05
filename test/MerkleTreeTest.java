import java.util.Arrays;
import java.util.List;

public class MerkleTreeTest {
    public static void run() {
        System.out.println("-- MerkleTreeTest --");

        List<String> leaves = Arrays.asList("a", "b", "c", "d", "e");
        MerkleTree tree = new MerkleTree(leaves);
        String root = tree.getRootHash();
        TestHarness.assertTrue("root hash is non-empty", root != null && root.length() > 0);

        for (int i = 0; i < leaves.size(); i++) {
            MerkleTree.MerkleProof proof = tree.getProof(i);
            boolean valid = MerkleTree.verifyProof(proof, leaves.get(i), root);
            TestHarness.assertTrue("inclusion proof verifies for leaf index " + i, valid);
        }

        // A proof for the wrong leaf value must fail.
        MerkleTree.MerkleProof proofForA = tree.getProof(0);
        boolean shouldFail = MerkleTree.verifyProof(proofForA, "not-a-real-leaf", root);
        TestHarness.assertTrue("proof correctly rejects a forged leaf value", !shouldFail);
    }
}
