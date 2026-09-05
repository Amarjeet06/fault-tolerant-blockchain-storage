import java.util.ArrayList;
import java.util.List;

/** Minimal zero-dependency test harness (no JUnit needed to build/run). */
public class TestHarness {
    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void assertTrue(String name, boolean cond) {
        if (cond) { passed++; System.out.println("  [PASS] " + name); }
        else { failed++; failures.add(name); System.out.println("  [FAIL] " + name); }
    }

    public static void assertEquals(String name, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        assertTrue(name + " (expected=" + expected + ", actual=" + actual + ")", ok);
    }

    public static void summary() {
        System.out.println("\n=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.out.println("Failed: " + failures);
            System.exit(1);
        }
    }
}
