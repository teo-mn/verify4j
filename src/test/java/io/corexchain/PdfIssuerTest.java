package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class PdfIssuerTest extends TestCase {
    public void testIssue() throws Exception {
        PdfIssuer pdfIssuer = new PdfIssuer(
                "0xcc546a88db1af7d250a2f20dee42ec436f99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test_user",
                "https://node-testnet.corexchain.io",
                3305
        );
        Assert.assertEquals("test", pdfIssuer.issue("d20220501",
                "/home/surenbayar/Downloads/30/106-real-test.pdf", "/home/surenbayar/Downloads/test2.pdf",
                null, "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e"));
//        Assert.assertEquals("6f5e2bd37d12d6ec24e41cadc375688d4cbb07cd048fc89e500e9cdcc20ab6c6", pdfIssuer.revoke("/home/surenbayar/Downloads/test.pdf",
//                "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e"));
    }

    public void testVerifier() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        PdfIssuer pdfIssuer = new PdfIssuer(
                "0xcc546a88db1af7d250a2f20dee42ec436f99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test_user",
                "https://node-testnet.corexchain.io",
                3305
        );
        VerifyResult result = pdfIssuer.verifyPdf("/home/surenbayar/Downloads/test2.pdf");
        Assert.assertEquals("ISSUED", result.state);
        Assert.assertEquals("test_user", result.issuerName);
    }
}