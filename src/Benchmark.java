public class Benchmark {
    public static void run(Blockchain blockchain) {
        long start = System.nanoTime();
        blockchain.queryRangeByYear(2010, 2015);
        long duration = System.nanoTime() - start;
        System.out.println("Query time: " + (duration/1_000_000) + "ms");

        long totalSize = blockchain.nodes.stream()
            .flatMap(n -> n.fragments.stream())
            .mapToLong(f -> f.length)
            .sum();
        System.out.println("Total storage: " + totalSize + " bytes");
    }
}