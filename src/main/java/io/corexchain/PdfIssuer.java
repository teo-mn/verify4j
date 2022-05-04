package io.corexchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tuples.generated.Tuple2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
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
        File file = new File(sourceFilePath);
        PDDocument pdf = PDDocument.load(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        COSDictionary object = (COSDictionary) pdf.getDocument().getTrailer().getDictionaryObject(COSName.INFO);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> issuer = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        issuer.put("name", this.issuerName);
        address.put("address", this.issuerAddress);
        issuer.put("identity", address);

        metadata.put("name", this.issuerName);
        metadata.put("certNum", id);

        object.setItem("issuer", new COSString(mapper.writeValueAsString(issuer)));
        object.setItem("metadata", new COSString(mapper.writeValueAsString(metadata)));
        object.setItem("chainpoint_proof", new COSString(""));
        object.setItem("version", new COSString(VERSION));
        pdf.save(byteArrayOutputStream);
        String hash = this.calcHash(byteArrayOutputStream.toByteArray());

        Tuple2<String, String> result = this.issue(id, hash, expireDate, desc, wallet);
        object.setItem("chainpoint_proof", new COSString("/CHAINPOINTSTART" + result.component2() + "/CHAINPOINTEND"));
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        pdf.save(byteArrayOutputStream2);
        FileOutputStream fos = new FileOutputStream(new File(destinationFilePath));
        byteArrayOutputStream2.writeTo(fos);
        pdf.close();
        return result.component1();
    }

    public String revoke(
            String filePath,
            String revokerName, String privateKey) throws IOException, NoSuchAlgorithmException {

        Credentials wallet = Credentials.create(privateKey);
        return this.revoke(filePath, revokerName, wallet);
    }

    public String revoke(
            String filePath,
            String revokerName, String keyStoreFile, String passphrase) throws IOException, CipherException, NoSuchAlgorithmException {

        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revoke(filePath, revokerName, wallet);
    }


    private String revoke(
            String filePath,
            String revokerName,
            Credentials wallet
    ) throws IOException, NoSuchAlgorithmException {
        File file = new File(filePath);
        PDDocument pdf = PDDocument.load(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        COSDictionary object = (COSDictionary) pdf.getDocument().getTrailer().getDictionaryObject(COSName.INFO);

        object.setItem("chainpoint_proof", new COSString(""));
        pdf.save(byteArrayOutputStream);
        String hash = this.calcHash(byteArrayOutputStream.toByteArray());

        System.out.println(hash);
        return hash;
    }

    public String calcFileHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(Files.readAllBytes(Paths.get(filePath)));
        byte[] digest = md.digest();
        return new String(Hex.encode(digest));
    }

    public String calcHash(byte[] val) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance(this.hashType);
        return new String(Hex.encode(digest.digest(val)));
    }
}
