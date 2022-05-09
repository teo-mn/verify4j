package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;
import org.web3j.tuples.generated.Tuple2;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class IssuerTest extends TestCase {
    Issuer issuer = new Issuer(
            "0xcc546a88db1af7d250a2f20dee42ec436f99e075",
            "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
            "test_user",
            "https://node-testnet.corexchain.io",
            3305
    );
    public void testIssueAndVerify() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException, InterruptedException {
//        Date now = new Date();
//        Date expire = new Date(now.getTime() + 3 * 1000); // 5 seconds later
//        Tuple2<String, String> res = issuer.issue("d20220501",
//                "19995e30DAB8E3F9113e216EEB2f44f6B8eb5756", expire, "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e");
//        TimeUnit.SECONDS.sleep(3);
//        System.out.println(res.component2());
        VerifyResult result = issuer.verify("19995e30DAB8E3F9113e216EEB2f44f6B8eb5756", "{\"targetHash\":\"19995e30dab8e3f9113e216eeb2f44f6b8eb5756\",\"merkleRoot\":\"19995e30dab8e3f9113e216eeb2f44f6b8eb5756\",\"proof\":[],\"anchors\":[{\"sourceId\":\"0x85ad31748eb6460d919a8559e39a04f959df29c1ef45c9f94c4e28959b30c085\",\"type\":\"CorexDataTest\"}],\"type\":\"ChainpointSHA256v2\",\"@context\":\"https://w3id.org/chainpoint/v2\"}");
        Assert.assertEquals("EXPIRED", result.getState());
    }

    public void testGetChainId() {
        Assert.assertEquals(3305, issuer.getChainId());
    }

    public void testSetChainId() {
        issuer.setChainId(1104);
        Assert.assertEquals(1104, issuer.getChainId());
    }

    public void testGetHashType() {
        Assert.assertEquals("SHA-256", issuer.getHashType());
    }

    public void testSetHashType() {
        issuer.setHashType("SHA-512");
        Assert.assertEquals("SHA-512", issuer.getHashType());
    }

    public void testIssue() {
        Issuer issuer = new Issuer(
                "0xcc546a88db1af7d250a2f20dee42ec436f99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test_user",
                "https://node-testnet.corexchain.io"
        );
        Assert.assertEquals(1104, issuer.getChainId());
    }

    public void testTestIssue() {
    }

    public void testTestIssue1() {
    }

    public void testTestIssue2() {
    }

    public void testTestIssue3() {
    }

    public void testTestIssue4() {
    }

    public void testVerify() {
    }

    public void testRevoke() {
    }

    public void testTestRevoke() {
    }

    public void testTestRevoke1() {
    }

    public void testGetByMerkleRoot() {
    }

    public void testGetCreditNumber() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Assert.assertEquals(Integer.valueOf(9999991), issuer.getCreditNumber("0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730"));
    }
}