import java.nio.file.*;
import java.util.*;

/**
 * Larger-scale benchmark against a synthetic 10,000-record dataset
 * (data/bench_10k.csv) to give more meaningful throughput numbers than the
 * 30-row demo dataset. Run with: java -cp build/classes ScaleBenchmark
 */
public class ScaleBenchmark {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("data/bench_10k.csv"));
        System.out.println("Dataset: " + lines.size() + " records\n");

        // ---- Write throughput: build the full chain from scratch ----------
        long writeStart = System.nanoTime();
        Blockchain bc = new Blockchain();
        for (String line : lines) {
            bc.addBlock(line.trim());
        }
        long writeElapsed = System.nanoTime() - writeStart;
        double writeOpsPerSec = lines.size() / (writeElapsed / 1_000_000_000.0);
        System.out.printf("Write: built %d-block chain in %.1f ms (%.0f blocks/sec, %.2f us/block)%n",
                lines.size(), writeElapsed / 1_000_000.0, writeOpsPerSec, (writeElapsed / 1000.0) / lines.size());

        boolean chainOk = bc.verifyChain();
        System.out.println("Chain integrity after " + lines.size() + " inserts: " + (chainOk ? "PASSED" : "FAILED"));

        long totalBytes = bc.nodes.stream().flatMap(n -> n.fragments.stream()).mapToLong(f -> f.length).sum();
        System.out.printf("Total fragment storage across 6 nodes: %.2f MB (encoded, 4 data + 2 parity shards)%n",
                totalBytes / (1024.0 * 1024.0));

        // ---- Read throughput WITHOUT failures ------------------------------
        int readIterations = 200;
        long readStart = System.nanoTime();
        for (int i = 0; i < readIterations; i++) {
            bc.queryRangeByYear(1901 + i, 1901 + i); // distinct ranges to defeat caching
        }
        long readElapsed = System.nanoTime() - readStart;
        System.out.printf("Read (healthy, %d distinct single-year queries over %d blocks): %.0f queries/sec (%.3f ms/query)%n",
                readIterations, lines.size(), readIterations / (readElapsed / 1_000_000_000.0),
                (readElapsed / 1_000_000.0) / readIterations);

        // ---- Read throughput WITH 2 nodes down (fault-tolerant path) -------
        Blockchain bcFailed = new Blockchain();
        for (String line : lines) bcFailed.addBlock(line.trim());
        bcFailed.simulateNodeFailure(1);
        bcFailed.simulateNodeFailure(4);
        long readFailStart = System.nanoTime();
        for (int i = 0; i < readIterations; i++) {
            bcFailed.queryRangeByYear(1901 + i, 1901 + i);
        }
        long readFailElapsed = System.nanoTime() - readFailStart;
        System.out.printf("Read (2/6 nodes DOWN, same %d queries): %.0f queries/sec (%.3f ms/query)%n",
                readIterations, readIterations / (readFailElapsed / 1_000_000_000.0),
                (readFailElapsed / 1_000_000.0) / readIterations);

        // ---- Correctness at scale: every record recoverable with 2 nodes down
        int mismatches = 0;
        for (int y = 1901; y < 1901 + 124; y++) {
            List<String> res = bcFailed.queryRangeByYear(y, y);
            for (String r : res) {
                if (r.startsWith("[")) mismatches++;
            }
        }
        System.out.println("Decode errors across full dataset with 2/6 nodes down: " + mismatches + " (expected 0)");
    }
}
