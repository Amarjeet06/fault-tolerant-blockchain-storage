package com.backblaze.erasure;

public class BlockVersion {
    public String data;
    public String hash;
    public String prevHash;

    public BlockVersion(String data, String prevHash) {
        this.data = data;
        this.prevHash = prevHash;
        this.hash = applySha256(data + prevHash);
    }

    public boolean isValid() {
        return hash.equals(applySha256(data + prevHash));
    }

    public static String applySha256(String input){
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Data: " + data + ", Hash: " + hash + ", PrevHash: " + prevHash;
    }
}