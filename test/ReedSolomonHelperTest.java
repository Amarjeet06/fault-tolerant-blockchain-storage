public class ReedSolomonHelperTest {
    public static void run() {
        System.out.println("-- ReedSolomonHelperTest --");

        String original = "2010,Physics,Test Winner,Country,Male";
        byte[][] shards = ReedSolomonHelper.encode(original);
        TestHarness.assertEquals("encode produces 6 shards", 6, shards.length);

        // No failures: decode should return the original string.
        String decodedNoFailure = ReedSolomonHelper.decode(shards);
        TestHarness.assertEquals("decode with no failures returns original", original, decodedNoFailure);

        // 2 missing shards (within tolerance: 4 data + 2 parity => can lose any 2).
        byte[][] with2Missing = shards.clone();
        with2Missing[1] = null;
        with2Missing[4] = null;
        String decodedWith2Missing = ReedSolomonHelper.decode(with2Missing);
        TestHarness.assertEquals("decode reconstructs correctly with 2 shards missing", original, decodedWith2Missing);

        // 2 missing data shards specifically (worst case, not just parity).
        byte[][] with2DataMissing = shards.clone();
        with2DataMissing[0] = null;
        with2DataMissing[2] = null;
        String decodedWith2DataMissing = ReedSolomonHelper.decode(with2DataMissing);
        TestHarness.assertEquals("decode reconstructs correctly when 2 DATA shards are missing",
                original, decodedWith2DataMissing);

        // 3 missing shards: beyond tolerance, must fail loudly rather than
        // returning corrupted data.
        byte[][] with3Missing = shards.clone();
        with3Missing[0] = null;
        with3Missing[2] = null;
        with3Missing[5] = null;
        boolean threw = false;
        try {
            ReedSolomonHelper.decode(with3Missing);
        } catch (IllegalStateException e) {
            threw = true;
        }
        TestHarness.assertTrue("decode throws (not silently corrupts) when 3 shards missing", threw);
    }
}
