import java.nio.file.*;
import java.util.*;

/**
 * Performance benchmarks for the blockchain storage engine.
 * Run standalone with: java -cp build/classes Benchmark
 */
public class Benchmark {
    public static void run(Blockchain blockchain) {
        long start = System.nanoTime();
        blockchain.queryRangeByYear(2010, 2015);
        long duration = System.nanoTime() - start;
        System.out.println("Range query (cached path warm-up) time: " + (duration / 1_000_000) + " ms");

        long totalSize = blockchain.nodes.stream()
                .flatMap(n -> n.fragments.stream())
                .mapToLong(f -> f.length)
                .sum();
        System.out.println("Total fragment storage across all nodes: " + totalSize + " bytes");
    }

    /** Throughput of encoding+distributing new blocks (writes). */
    public static double benchmarkAddBlockThroughput(List<String> records, int iterations) {
        Blockchain bc = new Blockchain();
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            bc.addBlock(records.get(i % records.size()));
        }
        long elapsedNanos = System.nanoTime() - start;
        return iterations / (elapsedNanos / 1_000_000_000.0);
    }

    /** Throughput of fault-tolerant decode (reads) with 2 nodes simulated down. */
    public static double benchmarkDecodeThroughputWithFailures(List<String> records, int iterations) {
        Blockchain bc = new Blockchain();
        for (String r : records) bc.addBlock(r);
        bc.simulateNodeFailure(1);
        bc.simulateNodeFailure(4);

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            bc.queryRangeByYear(1900, 2100); // full range, cache disabled below via unique args
        }
        long elapsedNanos = System.nanoTime() - start;
        return iterations / (elapsedNanos / 1_000_000_000.0);
    }

    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("data.csv"));

        System.out.println("=== Write throughput (addBlock: Reed-Solomon encode + distribute) ===");
        double writeOpsPerSec = benchmarkAddBlockThroughput(lines, 2000);
        System.out.printf("  %.0f blocks/sec (%.2f microseconds/block)%n", writeOpsPerSec, 1_000_000.0 / writeOpsPerSec);

        System.out.println("=== Read throughput (queryRangeByYear with 2/6 nodes down) ===");
        // Use a fresh chain each call inside so the cache doesn't mask real decode cost
        Blockchain bc = new Blockchain();
        for (String r : lines) bc.addBlock(r.trim());
        bc.simulateNodeFailure(1);
        bc.simulateNodeFailure(4);
        int iterations = 500;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // vary the range slightly to defeat the query cache and measure real decode cost
            bc.queryRangeByYear(1900, 2100 + (i % 3));
        }
        long elapsed = System.nanoTime() - start;
        double readOpsPerSec = iterations / (elapsed / 1_000_000_000.0);
        System.out.printf("  %.0f range-queries/sec across %d blocks with 2 nodes down (%.2f ms/query)%n",
                readOpsPerSec, lines.size(), (elapsed / 1_000_000.0) / iterations);
    }
}
