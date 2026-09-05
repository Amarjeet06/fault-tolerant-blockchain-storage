import com.backblaze.erasure.ReedSolomon;
import java.util.Arrays;

/**
 * Thin wrapper around Backblaze's ReedSolomon (4 data + 2 parity) erasure
 * coder used to fragment block data across storage nodes and reconstruct it
 * when up to 2 of the 6 fragments are missing.
 */
public class ReedSolomonHelper {
    public static final int DATA_SHARDS = 4;
    public static final int PARITY_SHARDS = 2;
    public static final int TOTAL_SHARDS = DATA_SHARDS + PARITY_SHARDS;

    public static byte[][] encode(String data) {
        byte[] bytes = data.getBytes();
        int shardSize = (int) Math.ceil((double) bytes.length / DATA_SHARDS);
        if (shardSize == 0) shardSize = 1; // guard against empty strings
        byte[][] shards = new byte[TOTAL_SHARDS][shardSize];

        for (int i = 0; i < DATA_SHARDS; i++) {
            int start = i * shardSize;
            int end = Math.min(start + shardSize, bytes.length);
            if (end > start) {
                System.arraycopy(bytes, start, shards[i], 0, end - start);
            }
        }

        ReedSolomon rs = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        rs.encodeParity(shards, 0, shardSize);

        return shards;
    }

    /**
     * Reconstructs the original string from up to TOTAL_SHARDS fragments.
     * Entries in {@code shards} that are {@code null} are treated as
     * missing/unavailable (e.g. from a failed node) and are reconstructed
     * in-place by the erasure decoder, provided at least DATA_SHARDS
     * fragments are actually present.
     *
     * NOTE: earlier versions of this method always passed shardPresent=true
     * for every slot regardless of nulls, which caused a NullPointerException
     * as soon as any node had actually failed instead of correctly
     * reconstructing the missing fragment. Fixed to detect real presence.
     */
    public static String decode(byte[][] shards) {
        if (shards.length != TOTAL_SHARDS) {
            throw new IllegalArgumentException("Expected " + TOTAL_SHARDS + " shards, got " + shards.length);
        }

        boolean[] shardPresent = new boolean[TOTAL_SHARDS];
        int shardSize = -1;
        int presentCount = 0;
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            if (shards[i] != null) {
                shardPresent[i] = true;
                presentCount++;
                if (shardSize == -1) shardSize = shards[i].length;
            }
        }
        if (shardSize == -1) {
            throw new IllegalStateException("All shards missing; cannot determine shard size");
        }
        if (presentCount < DATA_SHARDS) {
            throw new IllegalStateException(
                "Insufficient shards to reconstruct: need >= " + DATA_SHARDS + ", have " + presentCount);
        }

        // Allocate empty buffers for missing shards so the decoder has
        // somewhere to write the reconstructed bytes.
        byte[][] workingShards = new byte[TOTAL_SHARDS][];
        for (int i = 0; i < TOTAL_SHARDS; i++) {
            workingShards[i] = shards[i] != null ? shards[i] : new byte[shardSize];
        }

        ReedSolomon rs = ReedSolomon.create(DATA_SHARDS, PARITY_SHARDS);
        rs.decodeMissing(workingShards, shardPresent, 0, shardSize);

        byte[] combined = new byte[shardSize * DATA_SHARDS];
        for (int i = 0; i < DATA_SHARDS; i++) {
            System.arraycopy(workingShards[i], 0, combined, i * shardSize, shardSize);
        }
        return new String(combined).trim();
    }
}
