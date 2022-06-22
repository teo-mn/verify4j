package io.corexchain;

import junit.framework.TestCase;
import org.junit.Assert;

public class JsonIssuerTest extends TestCase {
    public void test0() throws Exception {
        JsonIssuer issuer = new JsonIssuer("0xCc546a88Db1aF7d250a2F20Dee42eC436F99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test",
                "https://node-testnet.corexchain.io",
                3305);
//        when(issuer.issue(anyString(), anyString(), any(), anyString(), any(Credentials.class)))
//                .thenReturn(new Tuple2<>("a", "b"));
        String res = issuer.issue("test", "./src/test/java/io/corexchain/1.json",
                "/home/surenbayar/test.json", null, "",
                "2", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e");
        System.out.println(res);
    }

    public void test1() throws Exception {
        JsonIssuer issuer = new JsonIssuer("0xCc546a88Db1aF7d250a2F20Dee42eC436F99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test",
                "https://node-testnet.corexchain.io",
                3305);
//        when(issuer.issue(anyString(), anyString(), any(), anyString(), any(Credentials.class)))
//                .thenReturn(new Tuple2<>("a", "b"));
        String res = issuer.revokeJson("/home/surenbayar/test.json",
                "", "a737d20b2e2a001bbf54c7edfcbffb015b0e67924e20f561c238ddaad6c4ed0e");
        System.out.println(res);
    }

    public void test2() throws Exception {
        JsonIssuer issuer = new JsonIssuer("0xCc546a88Db1aF7d250a2F20Dee42eC436F99e075",
                "0x89995e30DAB8E3F9113e216EEB2f44f6B8eb5730",
                "test",
                "https://node-testnet.corexchain.io");
//        when(issuer.issue(anyString(), anyString(), any(), anyString(), any(Credentials.class)))
//                .thenReturn(new Tuple2<>("a", "b"));
        VerifyResult res = issuer.verifyJson("/home/surenbayar/test.json");
        System.out.println(res.getMetadata());
        Assert.assertEquals(res.getState(), "ISSUED");
    }

}
