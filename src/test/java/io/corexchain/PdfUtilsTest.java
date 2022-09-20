package io.corexchain;

import io.corexchain.verify4j.PdfUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class PdfUtilsTest extends TestCase {
    public void testHash0() throws Exception {
        PdfUtils pdfUtils = new PdfUtils("src/test/java/io/corexchain/test.pdf");
        Assert.assertEquals("b3cc76639d59e88c3dd21498b11787d5dc8d3f985c7801a6797f0ff9dbe5cd41",
                pdfUtils.calcHash("SHA-256"));
        pdfUtils.close();
    }

    public void testHash1() throws Exception {
        PdfUtils pdfUtils = new PdfUtils("src/test/java/io/corexchain/test.pdf");
        pdfUtils.setMetaData("test_key", "");
        Assert.assertEquals("85341f65bd4acd94381cbd9693a3c379e0203e480a4223303d09257ec1e6becc",
                pdfUtils.calcHash("SHA-256"));
        pdfUtils.close();
    }

    public void testHash2() throws Exception {
        PdfUtils pdfUtils = new PdfUtils("src/test/java/io/corexchain/test.pdf");
        pdfUtils.setMetaData("test_key", "test_value");
        Assert.assertEquals("33ad0413dc6b8649c0c12fd9fd78555055e1288c0fb8bf421157c538f8a2f6e6",
                pdfUtils.calcHash("SHA-256"));
        pdfUtils.close();
    }

    public void testHash3() throws Exception {
        PdfUtils pdfUtils = new PdfUtils("src/test/java/io/corexchain/test.pdf");
        pdfUtils.setMetaData("test_key", "test_value");
        pdfUtils.save("src/test/java/io/corexchain/test2.pdf");
        pdfUtils.close();

        PdfUtils pdfUtils2 = new PdfUtils("src/test/java/io/corexchain/test2.pdf");

        Assert.assertEquals("33ad0413dc6b8649c0c12fd9fd78555055e1288c0fb8bf421157c538f8a2f6e6",
                pdfUtils2.calcHash("SHA-256"));

        pdfUtils2.setMetaData("test_key", "");
        Assert.assertEquals("85341f65bd4acd94381cbd9693a3c379e0203e480a4223303d09257ec1e6becc",
                pdfUtils2.calcHash("SHA-256"));
    }

    public void testHash4() throws Exception {
        PdfUtils pdfUtils = new PdfUtils("src/test/java/io/corexchain/test.pdf");

        pdfUtils.setMetaData("chainpoint_proof", "");
        pdfUtils.setMetaData("version", "1");
        pdfUtils.setMetaData("metadata", "{\"name\": \"test\", \"certNum\": \"1\"}");
        pdfUtils.setMetaData("issuer", "{\"name\": \"test\", \"identity\": {\"address\": \"0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730\"}}");
        Assert.assertEquals("924bd7ca894c5b7805a812c6c7560a6e4879e8c885c13601123fdfd2b0678851",
                pdfUtils.calcHash("SHA-256"));

        pdfUtils.setMetaData("chainpoint_proof", "/CHAINPOINTSTART{\"@context\": \"https://w3id.org/chainpoint/v2\", \"type\": \"ChainpointSHA256v2\", \"targetHash\": \"924bd7ca894c5b7805a812c6c7560a6e4879e8c885c13601123fdfd2b0678851\", \"merkleRoot\": \"924bd7ca894c5b7805a812c6c7560a6e4879e8c885c13601123fdfd2b0678851\", \"proof\": [], \"anchors\": [{\"type\": \"CorexDataMain\", \"sourceId\": \"0x2675db34fd192222318cf5c33e3e13de15c50d4ec6bf4b55c4afa3a005f972e3\"}]}/CHAINPOINTEND");

        pdfUtils.save("src/test/java/io/corexchain/test2.pdf");
        pdfUtils.close();
    }
}