import com.backblaze.erasure.ReedSolomon;
import java.util.Arrays;

public class ReedSolomonHelper {
    public static byte[][] encode(String data) {
        byte[] bytes = data.getBytes();
        int shardSize = (int) Math.ceil((double) bytes.length / 4);
        byte[][] shards = new byte[6][shardSize];

        for (int i = 0; i < 4; i++) {
            int start = i * shardSize;
            int end = Math.min(start + shardSize, bytes.length);
            System.arraycopy(bytes, start, shards[i], 0, end - start);
        }

        ReedSolomon rs = ReedSolomon.create(4, 2);
        rs.encodeParity(shards, 0, shardSize);

        return shards;
    }

    public static String decode(byte[][] shards) {
        ReedSolomon rs = ReedSolomon.create(4, 2);
        boolean[] shardPresent = new boolean[6];
        Arrays.fill(shardPresent, true);
        rs.decodeMissing(shards, shardPresent, 0, shards[0].length);

        byte[] combined = new byte[shards[0].length * 4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(shards[i], 0, combined, i * shards[0].length, shards[0].length);
        }
        return new String(combined).trim();
    }
}