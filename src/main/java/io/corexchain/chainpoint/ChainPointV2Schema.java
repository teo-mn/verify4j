package io.corexchain.chainpoint;

import java.util.ArrayList;
import java.util.Map;

public class ChainPointV2Schema {
    public String getTargetHash() {
        return targetHash;
    }

    public void setTargetHash(String targetHash) {
        this.targetHash = targetHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public ArrayList<AnchorSchema> getAnchors() {
        return anchors;
    }

    public void setAnchors(ArrayList<AnchorSchema> anchors) {
        this.anchors = anchors;
    }

    public ArrayList<Map<String, String>> getProof() {
        return proof;
    }

    public void setProof(ArrayList<Map<String, String>> proof) {
        this.proof = proof;
    }

    String targetHash;
    String merkleRoot;
    String type;
    String context;
    ArrayList<AnchorSchema> anchors;
    ArrayList<Map<String, String>> proof;
}

