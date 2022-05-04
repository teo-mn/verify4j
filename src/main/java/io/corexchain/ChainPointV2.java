package io.corexchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.Hash;

import java.util.*;

public class ChainPointV2 extends MerkleTree {

    public ChainPointV2() {
        super("SHA-256");
    }

    public ChainPointV2(String hashType) {
        super(hashType);
    }

    public static String CHAINPOINT_CONTEXT = "https://w3id.org/chainpoint/v2";

    // this works for up to 10 elements:
    public static Map<String, String> CHAINPOINT_HASH_TYPES = Map.of(
            "SHA-224", "ChainpointSHA224v2",
            "SHA-256", "ChainpointSHA256v2",
            "SHA-384", "ChainpointSHA384v2",
            "SHA-512", "ChainpointSHA512v2",
            "SHA3-224", "ChainpointSHA3-224v2",
            "SHA3-256", "ChainpointSHA3-256v2",
            "SHA3-384", "ChainpointSHA3-384v2",
            "SHA3-512", "ChainpointSHA3-512v2"
    );
    public static Map<String, String> CHAINPOINT_ANCHOR_TYPES = Map.of(
            "corex", "CorexDataMain",
            "corex_testnet", "CorexDataTest"
    );  //"eth", "ETHData"


    public String getChainPointHashType() {
        return CHAINPOINT_HASH_TYPES.get(this.hashType);
    }

    public String getReceipt(int index, String sourceId) throws JsonProcessingException {
        return this.getReceipt(index, sourceId, false);
    }

    public String getReceipt(int index, String sourceId, boolean testNet) throws JsonProcessingException {
        String chainType = testNet ? "corex_testnet" : "corex";
        if (this.getReady()) {
            Map<String, Object> map = new HashMap<>();
            map.put("@context", CHAINPOINT_CONTEXT);
            map.put("type", this.getChainPointHashType());
            map.put("targetHash", this.getLeaf(index));
            map.put("merkleRoot", this.getMerkleRoot());
            map.put("proof", this.getProof(index));
            Map<String, String> anchor = new HashMap<>();
            anchor.put("type", CHAINPOINT_ANCHOR_TYPES.get(chainType));
            anchor.put("sourceId", sourceId);
            List<Object> anchors = new ArrayList<Object>();
            anchors.add(anchor);
            map.put("anchors", anchors);

            ObjectMapper mapper = new ObjectMapper();
            return  mapper.writeValueAsString(map);
        }
        return null;
    }
}
