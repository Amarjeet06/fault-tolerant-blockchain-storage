import java.nio.file.*;
import java.util.*;
import com.backblaze.erasure.AuthenticatedIndex;
import com.backblaze.erasure.BlockVersion;

public class Main {
    public static void main(String[] args) {
        AuthenticatedIndex index = new AuthenticatedIndex();

        try {
            List<String> lines = Files.readAllLines(Paths.get("data/data.csv"));
            System.out.println("Loaded " + lines.size() + " lines from CSV.\n");

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    String category = parts[1].trim();
                    String winner = parts[2].trim();

                    String key = winner;
                    String value = category;

                    index.addVersion(key, year, value);
                    System.out.println("Added: " + key + " | " + value + " | " + year);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }

        System.out.println("\nFull Authenticated Index:");
        index.printIndex();

        System.out.println("\nQuery: Keys A-Z, Years 2010-2015");
        Map<String, TreeMap<Integer, BlockVersion>> result = index.getRange("A", "Z", 2010, 2015);

        for (Map.Entry<String, TreeMap<Integer, BlockVersion>> entry : result.entrySet()) {
            String key = entry.getKey();
            for (Map.Entry<Integer, BlockVersion> versionEntry : entry.getValue().entrySet()) {
                System.out.println("Key: " + key + ", Year: " + versionEntry.getKey() +
                        ", Value: " + versionEntry.getValue().data +
                        ", Hash: " + versionEntry.getValue().hash);
            }
        }
    }
}
