package io.corexchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tuples.generated.Tuple2;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PdfIssuer extends Issuer {

    public static String VERSION = "v1.0-java";

    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost,
            long chainId) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, chainId);
    }

    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, 1104);
    }

    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String privateKey
    ) throws Exception {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, wallet);
    }

    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String keyStoreFile,
            String passphrase
    ) throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, wallet);
    }

    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            Credentials wallet
    ) throws IOException, NoSuchAlgorithmException {
        PdfUtils pdfUtils = new PdfUtils(sourceFilePath);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> issuer = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        issuer.put("name", this.issuerName);
        address.put("address", this.issuerAddress);
        issuer.put("identity", address);

        metadata.put("name", this.issuerName);
        metadata.put("certNum", id);

        pdfUtils.setMetaData("issuer", mapper.writeValueAsString(issuer));
        pdfUtils.setMetaData("metadata", mapper.writeValueAsString(metadata));
        pdfUtils.setMetaData("chainpoint_proof", "");
        pdfUtils.setMetaData("version", VERSION);

        String hash = pdfUtils.calcHash(this.hashType);

        Tuple2<String, String> result = this.issue(id, hash, expireDate, desc, wallet);
        pdfUtils.setMetaData("chainpoint_proof", "/CHAINPOINTSTART" + result.component2() + "/CHAINPOINTEND");
        pdfUtils.save(destinationFilePath);
        pdfUtils.close();
        return result.component1();
    }

    public String revokePdf(
            String filePath, String revokerName, String privateKey) throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.revokePdf(filePath, revokerName, wallet);
    }

    public String revokePdf(
            String filePath, String revokerName, String keyStoreFile, String passphrase)
            throws IOException, CipherException, NoSuchAlgorithmException {

        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revoke(filePath, revokerName, wallet);
    }


    private String revokePdf(
            String filePath,
            String revokerName,
            Credentials wallet
    ) throws IOException, NoSuchAlgorithmException {
        PdfUtils pdfUtils = new PdfUtils(filePath);
        pdfUtils.setMetaData("chainpoint_proof", "");
        String hashValue = pdfUtils.calcHash(this.hashType);
        pdfUtils.close();
        return this.revoke(hashValue, revokerName, wallet);
    }
}
