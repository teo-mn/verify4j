package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;

import java.security.NoSuchAlgorithmException;

public class IssuerTest extends TestCase {
    public void testIssue() throws NoSuchAlgorithmException {
        Issuer pdfIssuer = new Issuer(
                "0xcc546a88db1af7d250a2f20dee42ec436f99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test_user",
                "https://node-testnet.corexchain.io",
                3305
        );
        Assert.assertEquals("1", pdfIssuer.issue("d20220501",
                "89995e30DAB8E3F9113e216EEB2f44f6B8eb5755", null, "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e").component1());

    }

    public void testTestIssue() {
    }
}