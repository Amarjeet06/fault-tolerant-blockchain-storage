import java.util.List;

public class BlockchainTest {
    public static void run() {
        System.out.println("-- BlockchainTest --");

        Blockchain bc = new Blockchain();
        bc.addBlock("2010,Physics,Alice,USA,Female");
        bc.addBlock("2011,Chemistry,Bob,UK,Male");
        bc.addBlock("2012,Peace,Carol,France,Female");

        TestHarness.assertTrue("chain verifies as intact after inserts", bc.verifyChain());

        // Query with no failures.
        List<String> baseline = bc.queryRangeByYear(2010, 2012);
        TestHarness.assertEquals("baseline query returns all 3 blocks", 3, baseline.size());
        TestHarness.assertTrue("baseline query has no errors", baseline.stream().noneMatch(r -> r.startsWith("[")));

        // Simulate 2 node failures (within tolerance).
        bc.simulateNodeFailure(1);
        bc.simulateNodeFailure(4);
        List<String> withFailures = bc.queryRangeByYear(2010, 2012);
        long okCount = withFailures.stream().filter(r -> !r.startsWith("[")).count();
        TestHarness.assertEquals("all 3 blocks still recoverable with 2/6 nodes down", 3L, okCount);
        TestHarness.assertTrue("recovered data matches original exactly",
                withFailures.contains("2010,Physics,Alice,USA,Female")
                && withFailures.contains("2011,Chemistry,Bob,UK,Male")
                && withFailures.contains("2012,Peace,Carol,France,Female"));

        // Simulate a 3rd failure: system must degrade safely, not corrupt.
        Blockchain bc2 = new Blockchain();
        bc2.addBlock("2010,Physics,Alice,USA,Female");
        bc2.simulateNodeFailure(0);
        bc2.simulateNodeFailure(2);
        bc2.simulateNodeFailure(5);
        List<String> beyondTolerance = bc2.queryRangeByYear(2010, 2010);
        TestHarness.assertTrue("3 node failures correctly reported as unrecoverable, not corrupted",
                beyondTolerance.get(0).startsWith("[Insufficient"));

        // Tamper detection: mutate a block's data post-hoc without recomputing hash.
        Blockchain bc3 = new Blockchain();
        bc3.addBlock("2010,Physics,Alice,USA,Female");
        bc3.addBlock("2011,Chemistry,Bob,UK,Male");
        bc3.chain.get(0).data = "TAMPERED";
        TestHarness.assertTrue("verifyChain() detects tampering", !bc3.verifyChain());
    }
}
