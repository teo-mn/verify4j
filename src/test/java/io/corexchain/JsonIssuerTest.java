package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class JsonIssuerTest extends TestCase {
    JsonIssuer issuer = new JsonIssuer("0xcc546a88db1af7d250a2f20dee42ec436f99e075",
            "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
            "test_user7",
            "https://node-testnet.corexchain.io",
            3305);
    public void testIssue() throws Exception {

        System.out.println(issuer.issue("asd", "/home/surenbayar/1.json", "/home/surenbayar/test.json", null, "test", "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e"));
    }

    public void testIssuer() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        VerifyResult result = issuer.verifyJson("/home/surenbayar/test.json");
        Assert.assertEquals(result.getState() , "ISSUED");
    }
}
