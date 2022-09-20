package io.corexchain.verify4j.chainpoint;


import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerkleTree {
    protected String hashType;
    protected ArrayList<byte[]> leaves;
    protected ArrayList<ArrayList<byte[]>> levels;

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }

    private Boolean isReady;

    public MerkleTree() {
        this.leaves = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.hashType = "SHA-256";
    }

    public MerkleTree(String hashType) {
        this.leaves = new ArrayList<>();
        this.hashType = hashType;
    }

    public void addLeaf(List<String> values, Boolean doHash) throws NoSuchAlgorithmException {
        this.setReady(false);
        for (String val : values
        ) {
            byte[] temp;
            if (doHash) {
                temp = this.calcHash(val.getBytes(StandardCharsets.UTF_8));
            } else {
                temp = this.stringToBytes(val);
            }
            this.leaves.add(temp);
        }
    }

    public void addLeaf(List<String> values) throws NoSuchAlgorithmException {
        this.setReady(false);
        this.addLeaf(values, false);
    }

    public String getLeaf(Integer index) {
        return byteToString(this.leaves.get(index));
    }

    public Integer getLeafCount() {
        return this.leaves.size();
    }

    public String getMerkleRoot() {
        if (this.getReady()) {
            return byteToString(this.getTopLevel().get(0));
        } else return null;
    }

    public void makeTree() throws NoSuchAlgorithmException {
        this.setReady(false);
        if (this.getLeafCount() > 0) {
            this.levels = new ArrayList<>();
            this.levels.add(new ArrayList<>(this.leaves));
            while (this.levels.get(this.levels.size() - 1).size() > 1) {
                this.calculateNextLevel();
            }
        }
        this.setReady(true);
    }

    public List<Map<String, String>> getProof(Integer index) {
        if (this.levels == null || this.levels.isEmpty()
                || !this.getReady() || this.leaves.size() <= index || index < 0) {
            return null;
        }
        ArrayList<Map<String, String>> proof = new ArrayList<>();
        for (ArrayList<byte[]> currentLevel : this.levels) {
            if (currentLevel.size() - 1 == index && currentLevel.size() % 2 == 1) {
                index = index / 2;
                continue;
            }
            boolean isRightNode = (index % 2 == 1);
            int j = isRightNode ? index - 1 : index + 1;
            String siblingPos = isRightNode ? "left" : "right";
            String siblingValue = byteToString(currentLevel.get(j));
            Map<String, String> map = new HashMap<String, String>();
            map.put(siblingPos, siblingValue);
            proof.add(map);
            index = index / 2;
        }
        return proof;
    }

    public boolean validateProof(List<Map<String, String>> proof, String targetHash, String merkleRoot)
            throws NoSuchAlgorithmException {
        if (proof.size() == 0) {
            return targetHash.toLowerCase().equals(merkleRoot);
        }
        byte[] proofHash = stringToBytes(targetHash);

        for (Map<String, String> p : proof) {
            if (p.containsKey("left")) {
                proofHash = this.calcHash(concatArray(stringToBytes(p.get("left")), proofHash));
            } else {
                proofHash = this.calcHash(concatArray(proofHash, stringToBytes(p.get("right"))));
            }
        }
        return merkleRoot.equals(byteToString(proofHash));
    }


    private ArrayList<byte[]> getTopLevel() {
        return this.levels.get(this.levels.size() - 1);
    }

    private void calculateNextLevel() throws NoSuchAlgorithmException {
        ArrayList<byte[]> newList = new ArrayList<>();
        ArrayList<byte[]> topLevel = this.levels.get(this.levels.size() - 1);
        for (int i = 0; i < topLevel.size(); i += 2) {
            if (i + 1 < topLevel.size()) {
                newList.add(this.calcHash(concatArray(topLevel.get(i), topLevel.get(i + 1))));
            } else {
                newList.add(topLevel.get(i));
            }
        }
        this.levels.add(newList);
    }

    private byte[] concatArray(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public byte[] stringToBytes(String a) {
        return Hex.decode(a);
    }

    public String byteToString(byte[] a) {
        return new String(Hex.encode(a)).toLowerCase();
    }


    public String calcHash(String val) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(this.hashType);
        final byte[] hashbytes = digest.digest(
                val.getBytes(StandardCharsets.UTF_8));
        return byteToString(hashbytes);
    }

    public byte[] calcHash(byte[] val) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(this.hashType);
        return digest.digest(val);
    }

    public static String calcHashFromStr(String value, String hashType) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(hashType);
        final byte[] hashbytes = digest.digest(
                value.getBytes(StandardCharsets.UTF_8));
        return  new String(Hex.encode(hashbytes)).toLowerCase();
    }

    public String mergeHash(String hash1, String hash2) throws NoSuchAlgorithmException {
        byte[] hash1Bytes = Hex.decode(hash1);
        byte[] hash2Bytes = Hex.decode(hash2);
        byte[] result = this.concatArray(hash1Bytes, hash2Bytes);
        byte[] resultHash = this.calcHash(result);
        return byteToString(resultHash);
    }
}
