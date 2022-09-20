package io.corexchain.verify4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.corexchain.verify4j.chainpoint.ChainPointV2;
import io.corexchain.verify4j.chainpoint.MerkleTree;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.tuples.generated.Tuple2;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

import static io.corexchain.verify4j.JsonUtils.jsonMapToString;

public class JsonIssuer extends Issuer {

    public JsonIssuer(String smartContractAddress, String issuerAddress, String issuerName, String nodeHost, long chainId) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, chainId);
    }

    public JsonIssuer(String smartContractAddress, String issuerAddress, String issuerName, String nodeHost) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost);
    }

    /**
     * JSON файлын хашийг ухаалаг гэрээнд бүртгэх.
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
     * JSON файлын хашийг ухаалаг гэрээнд бүртгэх.
     * Бүртгэсний дараа файлын мэтадата хэсэгт гүйлгээний мэдээлэл, chainpoint баталгаа зэргийг бичин хадгална
     *
     * @param id                  файлын ID /хоосон байж болно/
     * @param sourceFilePath      эх файлын зам
     * @param destinationFilePath бүртгэсний дараа мета дата бичээд хадгалах файлын зам
     * @param expireDate          дуусах огноо /null байж болно/
     * @param desc                тайлбар
     * @param additionalInfo      мэтадата дээр орох нэмэлт мэдээлэл
     * @param privateKey          нууц түлхүүр
     * @param mapper              мэтадата бичсэн файлыг хадгалах үед өөрсдийн тохируулсан mapper ашиглаж болно
     * @return Блокчэйний гүйлгээний ID буцаана
     */
    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String additionalInfo,
            String privateKey,
            ObjectMapper mapper
    ) throws Exception {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, additionalInfo, wallet, mapper);
    }

    /**
     * JSON файлын хашийг ухаалаг гэрээнд бүртгэх.
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

    /**
     * JSON файлын хашийг ухаалаг гэрээнд бүртгэх.
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
     * @param mapper              мэтадата бичсэн файлыг хадгалах үед өөрсдийн тохируулсан mapper ашиглаж болно
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
            String passphrase,
            ObjectMapper mapper
    ) throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, additionalInfo, wallet, mapper);
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
        ObjectMapper mapper = new ObjectMapper();
        return issue(id, sourceFilePath, destinationFilePath, expireDate, desc, additionalInfo, wallet, mapper);
    }


    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String additionalInfo,
            Credentials wallet,
            ObjectMapper mapper
    ) throws IOException, NoSuchAlgorithmException {
        Map<String, Object> json = mapper.readValue(new File(sourceFilePath), HashMap.class);
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

        json.put("verifymn", verifymn);

        String jsonStr = jsonMapToString(json);

        String hash = MerkleTree.calcHashFromStr(jsonStr, this.hashType);

        Tuple2<String, String> result = this.issue(id, hash, expireDate, desc, wallet);

        HashMap<String, Object> chainpoint = mapper.readValue(result.component2(), HashMap.class);
        verifymn.put("chainpointProof", chainpoint);
        json.put("verifymn", verifymn);
        mapper.writeValue(new File(destinationFilePath), json);
        return result.component1();
    }

    /**
     * JSON файлыг хүчингүй болгох
     *
     * @param filePath    файлын зам
     * @param revokerName хүчингүй болгож буй хүний нэр
     * @param privateKey  нууц түлхүүр
     * @return Гүйлгээний мэдээлэл
     */
    public String revokeJson(String filePath, String revokerName, String privateKey)
            throws IOException, NoSuchAlgorithmException {

        Credentials wallet = Credentials.create(privateKey);
        return this.revokeJson(filePath, revokerName, wallet);
    }

    /**
     * JSON файлыг хүчингүй болгох
     *
     * @param filePath     файлын зам
     * @param revokerName  хүчингүй болгож буй хүний нэр
     * @param keyStoreFile нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase   нууц түлхүүрийг сэргээх код
     * @return Гүйлгээний мэдээлэл
     */
    public String revokeJson(
            String filePath, String revokerName, String keyStoreFile, String passphrase)
            throws IOException, CipherException, NoSuchAlgorithmException {

        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revokeJson(filePath, revokerName, wallet);
    }

    private String revokeJson(String filePath, String revokerName, Credentials wallet) throws IOException, NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(new File(filePath), HashMap.class);
        Map<String, Object> verifymn = (Map<String, Object>) json.get("verifymn");
        verifymn.remove("chainpointProof");
        json.put("verifymn", verifymn);
        String hashValue = MerkleTree.calcHashFromStr(jsonMapToString(json), this.hashType);
        return this.revoke(hashValue, revokerName, wallet);
    }

    /**
     * Файл ухаалаг гэрээнд бүртгэгдсэн эсэхийг шалгана
     *
     * @param filePath issue хийсний дараа мэтадата бичигдсэн файлын зам
     * @return Хэрэв ухаалаг гэрээнд бүртгэлтэй бол `state` нь EXPIRED, ISSUED, REVOKED-ийн аль нэг утгыг авна.
     * `issuerName` нь бүртгэсэн байгууллагын нэр байна
     */
    public VerifyResult verifyJson(String filePath) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(new File(filePath), HashMap.class);
        Map<String, Object> verifymn = (Map<String, Object>) json.get("verifymn");
        String metadata = mapper.writeValueAsString(verifymn);
        verifymn.remove("chainpointProof");
        json.put("verifymn", verifymn);
        String hashValue = MerkleTree.calcHashFromStr(jsonMapToString(json), this.hashType);
        ChainPointV2 v2 = new ChainPointV2();
        v2.addLeaf(new ArrayList<String>(Collections.singleton(hashValue)));
        v2.makeTree();

        VerifyResult result = verify(hashValue, v2.getReceipt(0, ""));
        result.setMetadata(metadata);
        return result;
    }
}

