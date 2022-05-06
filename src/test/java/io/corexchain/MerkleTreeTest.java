package io.corexchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.corexchain.chainpoint.ChainPointV2;
import junit.framework.TestCase;
import org.junit.Assert;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MerkleTreeTest extends TestCase {
    public void test() throws NoSuchAlgorithmException, JsonProcessingException {
        ChainPointV2 mk = new ChainPointV2("SHA-256");
        List<String> data = Arrays.asList("1", "2", "3", "4", "5");
        mk.addLeaf(data, true);
        mk.makeTree();

        for (int i = 0; i < data.size(); i++) {
            List<Map<String, String>> proof = mk.getProof(i);
            Assert.assertTrue(mk.validateProof(proof, mk.calcHash(data.get(i)), mk.getMerkleRoot()));
        }
        Assert.assertEquals("80285644ea6e999deb6a60f1b4d16d03d611f46ffc1c390a929463cbe1c33c5c", mk.getMerkleRoot());
        Assert.assertEquals("{\"targetHash\":\"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b\",\"merkleRoot\":\"80285644ea6e999deb6a60f1b4d16d03d611f46ffc1c390a929463cbe1c33c5c\",\"proof\":[{\"right\":\"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35\"},{\"right\":\"20ab747d45a77938a5b84c2944b8f5355c49f21db0c549451c6281c91ba48d0d\"},{\"right\":\"ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d\"}],\"anchors\":[{\"sourceId\":\"test\",\"type\":\"CorexDataMain\"}],\"type\":\"ChainpointSHA256v2\",\"@context\":\"https://w3id.org/chainpoint/v2\"}", mk.getReceipt(0, "test"));

    }
}
