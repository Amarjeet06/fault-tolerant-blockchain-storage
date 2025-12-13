import java.util.Arrays;

public class Block {
    public int index;
    public String data;
    public String previousHash;
    public String hash;
    public byte[][] fragments;
    public long timestamp;

    public Block(int index, String data, String previousHash) {
        this.index = index;
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
        this.fragments = ReedSolomonHelper.encode(data);
    }

    public String calculateHash() {
        return StringUtil.applySha256(index + data + previousHash + timestamp);
    }

    public boolean verifyIntegrity() {
        return this.hash.equals(calculateHash());
    }
}