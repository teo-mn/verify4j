package io.corexchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.web3j.tuples.generated.Tuple2;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MerkleTreeTest extends TestCase {
    public void test() throws NoSuchAlgorithmException, JsonProcessingException {
        ChainPointV2 mk = new ChainPointV2("SHA-256");
        List<String> data = Arrays.asList("1", "2", "3", "4");
        mk.addLeaf(data, true);
        mk.makeTree();

        for (int i = 0; i < data.size(); i++) {
            List<Tuple2<String, String>> proof = mk.getProof(i);
            Assert.assertTrue(mk.validateProof(proof, mk.calcHash(data.get(i)), mk.getMerkleRoot()));
        }
        Assert.assertEquals("cd53a2ce68e6476c29512ea53c395c7f5d8fbcb4614d89298db14e2a5bdb5456", mk.getMerkleRoot());
        Assert.assertEquals("{\"targetHash\":\"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b\",\"anchor\":[{\"sourceId\":\"test\",\"type\":\"CorexDataMain\"}],\"merkleRoot\":\"cd53a2ce68e6476c29512ea53c395c7f5d8fbcb4614d89298db14e2a5bdb5456\",\"proof\":[{\"value1\":\"right\",\"value2\":\"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35\",\"size\":2},{\"value1\":\"right\",\"value2\":\"20ab747d45a77938a5b84c2944b8f5355c49f21db0c549451c6281c91ba48d0d\",\"size\":2}],\"type\":\"ChainpointSHA256v2\",\"@context\":\"https://w3id.org/chainpoint/v2\"}", mk.getReceipt(0, "test"));
        Assert.assertEquals("{\"targetHash\":\"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b\",\"anchor\":[{\"sourceId\":\"test\",\"type\":\"CorexDataTest\"}],\"merkleRoot\":\"cd53a2ce68e6476c29512ea53c395c7f5d8fbcb4614d89298db14e2a5bdb5456\",\"proof\":[{\"value1\":\"right\",\"value2\":\"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35\",\"size\":2},{\"value1\":\"right\",\"value2\":\"20ab747d45a77938a5b84c2944b8f5355c49f21db0c549451c6281c91ba48d0d\",\"size\":2}],\"type\":\"ChainpointSHA256v2\",\"@context\":\"https://w3id.org/chainpoint/v2\"}", mk.getReceipt(0, "test", true));
        Assert.assertEquals("{\"targetHash\":\"d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35\",\"anchor\":[{\"sourceId\":\"test\",\"type\":\"CorexDataMain\"}],\"merkleRoot\":\"cd53a2ce68e6476c29512ea53c395c7f5d8fbcb4614d89298db14e2a5bdb5456\",\"proof\":[{\"value1\":\"left\",\"value2\":\"6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b\",\"size\":2},{\"value1\":\"right\",\"value2\":\"20ab747d45a77938a5b84c2944b8f5355c49f21db0c549451c6281c91ba48d0d\",\"size\":2}],\"type\":\"ChainpointSHA256v2\",\"@context\":\"https://w3id.org/chainpoint/v2\"}", mk.getReceipt(1, "test"));

    }
}
