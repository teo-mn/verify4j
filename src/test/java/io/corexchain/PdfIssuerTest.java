package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;

public class PdfIssuerTest extends TestCase {
    public void testIssue() throws Exception {
        PdfIssuer pdfIssuer = new PdfIssuer(
                "0x3628b3a696162d8a632394FF39ea0BC5A29C8821",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test_user",
                "https://node-testnet.corexchain.io",
                3305
        );
        Assert.assertEquals("test", pdfIssuer.issue("d20220501",
                "/home/surenbayar/Downloads/30/102-real-test.pdf", "/home/surenbayar/Downloads/test2.pdf",
                null, "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e"));
        Assert.assertEquals("6f5e2bd37d12d6ec24e41cadc375688d4cbb07cd048fc89e500e9cdcc20ab6c6", pdfIssuer.revoke("/home/surenbayar/Downloads/test.pdf",
                "test", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e"));
    }
}