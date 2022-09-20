package io.corexchain.verify4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.corexchain.verify4j.chainpoint.ChainPointV2;
import org.apache.pdfbox.cos.COSString;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tuples.generated.Tuple2;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

public class PdfIssuer extends Issuer {

    /**
     * PDF файлын хашийг ухаалаг гэрээнд бүртгэх
     *
     * @param smartContractAddress ухаалаг гэрээний хаяг
     * @param issuerAddress        илгээгч байгууллагын блокчэйний хаяг
     * @param issuerName           илгээгч байгууллагын нэр
     * @param nodeHost             блокчэйний нөүдний URL
     * @param chainId              блокчэйн ID
     */
    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost,
            long chainId) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, chainId);
    }

    /**
     * PDF файлын хашийг ухаалаг гэрээнд бүртгэх
     *
     * @param smartContractAddress ухаалаг гэрээний хаяг
     * @param issuerAddress        илгээгч байгууллагын блокчэйний хаяг
     * @param issuerName           илгээгч байгууллагын нэр
     * @param nodeHost             блокчэйний нөүдний URL
     */
    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost);
    }

    /**
     * PDF файлын хашийг ухаалаг гэрээнд бүртгэх.
     * Бүртгэсний дараа файлын мэтадата хэсэгт гүйлгээний мэдээлэл, chainpoint баталгаа зэргийг бичин хадгална
     *
     * @param id                  файлын ID /хоосон байж болно/
     * @param sourceFilePath      эх файлын зам
     * @param destinationFilePath бүртгэсний дараа мета дата бичээд хадгалах файлын зам
     * @param expireDate          дуусах огноо /null байж болно/
     * @param desc                тайлбар
     * @param additionalInfo      мэтадата дээр орох нэмэлт мэдээлэл
     * @param privateKey          нууц түлхүүр
     * @return Блокчэйний гүйлгээний ID буцаана
     */
    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String additionalInfo,
            String privateKey
    ) throws Exception {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, additionalInfo, wallet);
    }

    /**
     * PDF файлын хашийг ухаалаг гэрээнд бүртгэх.
     * Бүртгэсний дараа файлын мэтадата хэсэгт гүйлгээний мэдээлэл, chainpoint баталгаа зэргийг бичин хадгална
     *
     * @param id                  файлын ID /хоосон байж болно/
     * @param sourceFilePath      эх файлын зам
     * @param destinationFilePath бүртгэсний дараа мета дата бичээд хадгалах файлын зам
     * @param expireDate          дуусах огноо /null байж болно/
     * @param desc                тайлбар
     * @param additionalInfo      мэтадата дээр орох нэмэлт мэдээлэл
     * @param keyStoreFile        нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase          нууц түлхүүрийг сэргээх код
     * @return Блокчэйний гүйлгээний ID буцаана
     */
    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String additionalInfo,
            String keyStoreFile,
            String passphrase
    ) throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, additionalInfo, wallet);
    }

    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String additionalInfo,
            Credentials wallet
    ) throws IOException, NoSuchAlgorithmException {
        PdfUtils pdfUtils = new PdfUtils(sourceFilePath);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> issuer = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        Map<String, String> blockchain = new HashMap<>();
        Map<String, Object> verifymn = new HashMap<>();

        /*
          verifymn: {
               issuer: {
                   name: "",
                   address: ""
               },
               info: {
                   name: "",
                   desc: "",
                   cerNum: "",
                   additionalInfo: ""
               },
               version: "",
               blockchain: {
                    network: "",
                    smartContractAddress: ""
               }
          }
         */
        issuer.put("name", this.issuerName);
        issuer.put("address", this.issuerAddress);

        info.put("name", this.issuerName);
        info.put("certNum", id);
        info.put("desc", desc);
        info.put("additionalInfo", additionalInfo);

        blockchain.put("network", this.chainId == 1104 ? "CorexMain" : "CorexTest");
        blockchain.put("smartContractAddress", this.smartContractAddress);

        verifymn.put("issuer", issuer);
        verifymn.put("info", info);
        verifymn.put("blockchain", blockchain);
        verifymn.put("version", VERSION);

        pdfUtils.setMetaData("verifymn", mapper.writeValueAsString(verifymn));

        String hash = pdfUtils.calcHash(this.hashType);

        Tuple2<String, String> result = this.issue(id, hash, expireDate, desc, wallet);
        pdfUtils.save(destinationFilePath);
        pdfUtils.close();
        return result.component1();
    }

    /**
     * Файл ухаалаг гэрээнд бүртгэгдсэн эсэхийг шалгана
     *
     * @param filePath issue хийсний дараа мэтадата бичигдсэн файлын зам
     * @return Хэрэв ухаалаг гэрээнд бүртгэлтэй бол `state` нь EXPIRED, ISSUED, REVOKED-ийн аль нэг утгыг авна.
     * `issuerName` нь бүртгэсэн байгууллагын нэр байна
     */
    public VerifyResult verifyPdf(String filePath)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        PdfUtils pdfUtils = new PdfUtils(filePath);
        String hashValue = PdfUtils.calcHash(filePath, this.hashType);
        pdfUtils.close();


        ChainPointV2 v2 = new ChainPointV2();
        v2.addLeaf(new ArrayList<String>(Collections.singleton(hashValue)));
        v2.makeTree();
        VerifyResult result = verify(hashValue, v2.getReceipt(0, ""));
        result.setMetadata(((COSString) pdfUtils.getMetaData("verifymn")).getString());
        return result;
    }

    /**
     * Бүртгэсэн файлыг хүчингүй болгох
     *
     * @param filePath    issue хийсний дараа мэтадата бичигдсэн файлын зам
     * @param revokerName хүчингүй болгож буй хүний нэр
     * @param privateKey  нууц түлхүүр
     * @return Блокчэйний гүйлгээний ID
     */
    public String revokePdf(
            String filePath, String revokerName, String privateKey) throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.revokePdf(filePath, revokerName, wallet);
    }

    /**
     * Бүртгэсэн файлыг хүчингүй болгох
     *
     * @param filePath     issue хийсний дараа мэтадата бичигдсэн файлын зам
     * @param revokerName  хүчингүй болгож буй хүний нэр
     * @param keyStoreFile нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase   нууц түлхүүрийг сэргээх код
     * @return Блокчэйний гүйлгээний ID
     */
    public String revokePdf(
            String filePath, String revokerName, String keyStoreFile, String passphrase)
            throws IOException, CipherException, NoSuchAlgorithmException {

        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revokePdf(filePath, revokerName, wallet);
    }


    private String revokePdf(
            String filePath,
            String revokerName,
            Credentials wallet
    ) throws IOException, NoSuchAlgorithmException {
        PdfUtils pdfUtils = new PdfUtils(filePath);
//        pdfUtils.setMetaData("chainpoint_proof", "");
        String hashValue = PdfUtils.calcHash(filePath, this.hashType);
        pdfUtils.close();
        return this.revoke(hashValue, revokerName, wallet);
    }
}
