public class RunAllTests {
    public static void main(String[] args) {
        ReedSolomonHelperTest.run();
        BlockchainTest.run();
        MerkleTreeTest.run();
        TestHarness.summary();
        System.out.println("\nAll tests passed.");
    }
}
