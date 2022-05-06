package io.corexchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.corexchain.chainpoint.ChainPointV2;
import io.corexchain.chainpoint.ChainPointV2Schema;
import io.corexchain.exceptions.*;
import io.nbc.contracts.CertificationRegistration;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;

public class Issuer {
    protected StaticGasProvider gasProvider;

    protected static BigInteger GAS_PRICE = BigInteger.valueOf((long) (1e12));
    protected static BigInteger GAS_LIMIT = BigInteger.valueOf(2000000L);
    protected String smartContractAddress;
    protected String issuerAddress;
    protected String issuerName;
    protected String nodeHost;
    private ChainPointV2 chainPointV2;

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    protected long chainId = 1104;

    public String getHashType() {
        return hashType;
    }

    public void setHashType(String hashType) {
        this.hashType = hashType;
    }

    protected String hashType = "SHA-256";

    /**
     * @param smartContractAddress ухаалаг гэрээний хаяг
     * @param issuerAddress        илгээгчийн хаяг
     * @param issuerName           илгээгчийн нэр
     * @param nodeHost             блокчэйний нөүдний URL
     * @param chainId              блокчэйн ID
     */
    public Issuer(String smartContractAddress,
                  String issuerAddress,
                  String issuerName,
                  String nodeHost,
                  long chainId) {
        this.smartContractAddress = Keys.toChecksumAddress(smartContractAddress);
        this.issuerAddress = Keys.toChecksumAddress(issuerAddress);
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        this.chainId = chainId;
    }

    /**
     * Verify хаш утгаар баталгаажуулагч
     *
     * @param smartContractAddress ухаалаг гэрээний хаяг
     * @param issuerAddress        үүсгэгчийн хаяг
     * @param issuerName           үүсгэгчийн нэр
     * @param nodeHost             блокчэйний нөүдний URL
     */
    public Issuer(String smartContractAddress,
                  String issuerAddress,
                  String issuerName,
                  String nodeHost) {
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
    }

    /**
     * Нэг хаш утга бүртгэх
     *
     * @param id           хаш утгатай хамааралтай ID /хоосон байж болно/
     * @param hashValue    хаш утга
     * @param expireDate   дуусах огноо /null байж болно/
     * @param desc         тайлбар /хоосон байж болно/
     * @param keyStoreFile нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase   нууц түлхүүрийг сэргээх код
     * @return эхний утга блокчэйний гүйлгээний ID, 2 дахь утга chainpoint баталгаа.
     * Chainpoint баталгааг ашиглаж verify хийх тул файлын мета дата зэрэг устхааргүй газар хадгалах хэрэгтэй
     */
    public Tuple2<String, String> issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc,
            String keyStoreFile,
            String passphrase
    ) throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, hashValue, expireDate, desc, wallet);
    }

    /**
     * Олон хаш утгыг merkle tree ашиглан нэг гүйлгээгээр бүртгэх
     *
     * @param id           хаш утгатай хамааралтай ID /хоосон байж болно/
     * @param hashValues   хаш утгуудын цуваа
     * @param expireDate   дуусах огноо /null байж болно/
     * @param desc         тайлбар /хоосон байж болно/
     * @param keyStoreFile нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase   нууц түлхүүрийг сэргээх код
     * @return эхний утга блокчэйний гүйлгээний ID, 2 дахь утга chainpoint баталгаа.
     * Chainpoint баталгааг ашиглаж verify хийх тул файлын мета дата зэрэг устхааргүй газар хадгалах хэрэгтэй
     */
    public Tuple2<String, String> issue(
            String id,
            ArrayList<String> hashValues,
            Date expireDate,
            String desc,
            String keyStoreFile,
            String passphrase
    ) throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, hashValues, expireDate, desc, wallet);
    }


    /**
     * Нэг хаш утга бүртгэх
     *
     * @param id         хаш утгатай хамааралтай ID /хоосон байж болно/
     * @param hashValue  хаш утга
     * @param expireDate дуусах огноо /null байж болно/
     * @param desc       тайлбар /хоосон байж болно/
     * @param privateKey нууц түлхүүр
     * @return эхний утга блокчэйний гүйлгээний ID, 2 дахь утга chainpoint баталгаа.
     * Chainpoint баталгааг ашиглаж verify хийх тул файлын мета дата зэрэг устхааргүй газар хадгалах хэрэгтэй
     */
    public Tuple2<String, String> issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc,
            String privateKey
    ) throws NoSuchAlgorithmException {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, hashValue, expireDate, desc, wallet);
    }

    /**
     * Нэг хаш утга бүртгэх
     *
     * @param id         хаш утгатай хамааралтай ID /хоосон байж болно/
     * @param hashValues хаш утгуудын цуваа
     * @param expireDate дуусах огноо /null байж болно/
     * @param desc       тайлбар /хоосон байж болно/
     * @param privateKey нууц түлхүүр
     * @return эхний утга блокчэйний гүйлгээний ID, 2 дахь утга chainpoint баталгаа.
     * Chainpoint баталгааг ашиглаж verify хийх тул файлын мета дата зэрэг устхааргүй газар хадгалах хэрэгтэй
     */
    public Tuple2<String, String> issue(
            String id,
            ArrayList<String> hashValues,
            Date expireDate,
            String desc,
            String privateKey
    ) throws NoSuchAlgorithmException {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, hashValues, expireDate, desc, wallet);
    }

    protected Tuple2<String, String> issue(String id,
                                           String hashValue,
                                           Date expireDate,
                                           String desc,
                                           Credentials wallet)
            throws NoSuchAlgorithmException {
        return this.issue(id, new ArrayList<>() {{
            add(hashValue);
        }}, expireDate, desc, wallet);
    }


    protected Tuple2<String, String> issue(String id,
                                           ArrayList<String> hashValues,
                                           Date expireDate,
                                           String desc,
                                           Credentials wallet)
            throws NoSuchAlgorithmException {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        CertificationRegistration smartContract = createSmartContractInstance(web3j, wallet);
        String root = getChainPointRoot(hashValues);

        try {
            BigInteger creditBalance = smartContract.getCredit(this.issuerAddress).send();
            if (creditBalance.compareTo(BigInteger.ZERO) == 0)
                throw new InvalidCreditAmountException("Not enough credit.");

            CertificationRegistration.Certification cert = smartContract.getCertification(root).send();
            if (cert.id.compareTo(BigInteger.ZERO) != 0)
                throw new AlreadyExistsException("Certification hash already exists in smart contract.");

            BigInteger exDate = expireDate != null ? BigInteger.valueOf(expireDate.getTime() / 1000) : BigInteger.ZERO;
            TransactionReceipt tr = smartContract.addCertification(root, id, exDate, "0", desc).send();
            if (!tr.isStatusOK()) {
                throw new BlockchainNodeException("Error occurred on blockchain.");
            }
            return new Tuple2<>(tr.getTransactionHash(), chainPointV2.getReceipt(0, tr.getTransactionHash(),
                    this.chainId != 1104));
        } catch (AlreadyExistsException | InvalidCreditAmountException | BlockchainNodeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BlockchainNodeException(ex.getMessage());
        }
    }

    /**
     * Хаш утга ухаалаг гэрээнд бүртгэгдсэн эсэхийг шалгана
     *
     * @param hashValue     хаш утга
     * @param chainPointStr issue хийх үед үүссэн chainpoint ийн баталгаа
     * @return Хэрэв ухаалаг гэрээнд бүртгэлтэй бол state нь EXPIRED, ISSUED, REVOKED-ийн аль нэг утгыг авна.
     */
    public VerifyResult verify(String hashValue, String chainPointStr)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
        CertificationRegistration smartContract = createSmartContractInstance(web3j, dummyCredentials);

        ObjectMapper mapper = new ObjectMapper();
        ChainPointV2Schema schema = mapper.readValue(chainPointStr, ChainPointV2Schema.class);
        ChainPointV2 chainPointV2 = new ChainPointV2();
        if (!chainPointV2.validateProof(schema.getProof(), hashValue, schema.getMerkleRoot())) {
            throw new InvalidChainPointException("Chain point doesn't match");
        }

        String root = schema.getMerkleRoot();

        try {
            CertificationRegistration.Certification cert = smartContract.getCertification(root).send();
            if (cert.id.compareTo(BigInteger.ZERO) == 0)
                throw new NotFoundException("Hash not found in smart contract");
            String state;
            Date d = new Date(cert.expireDate.longValue() * 1000);
            if (cert.expireDate.compareTo(BigInteger.ZERO) != 0 && d.before(new Date())) {
                state = "EXPIRED";
            } else if (cert.isRevoked) {
                state = "REVOKED";
            } else {
                state = "ISSUED";
            }
            return new VerifyResult(cert, "", state);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BlockchainNodeException(e.getMessage());
        }
    }

    /**
     * Ухаалаг гэрээнд бүртгэгдсэн мэдээллийг merkleRoot утгаар нь авах
     *
     * @param merkleRoot merkleRoot
     */
    public CertificationRegistration.Certification getByMerkleRoot(String merkleRoot)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
        CertificationRegistration smartContract = createSmartContractInstance(web3j, dummyCredentials);

        try {
            CertificationRegistration.Certification cert = smartContract.getCertification(merkleRoot).send();
            if (cert.id.compareTo(BigInteger.ZERO) == 0)
                throw new NotFoundException();
            return cert;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BlockchainNodeException();
        }
    }

    /**
     * Ухаалаг гэрээнд хичнээн кредит эзэмшиж байгааг шалгах
     *
     * @param address хэтэвчийн хаяг
     * @return кредитийн тоо
     */
    public Integer getCreditNumber(String address)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        if (!WalletUtils.isValidAddress(Keys.toChecksumAddress(address)))
            throw new InvalidAddressException("Wallet address is invalid.");

        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
        CertificationRegistration smartContract = createSmartContractInstance(web3j, dummyCredentials);

        try {
            BigInteger cert = smartContract.getCredit(Keys.toChecksumAddress(address)).send();
            return cert.intValue();
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BlockchainNodeException();
        }
    }


    /**
     * Бүртгэсэн нэг болон олон хашийг буцаан хүчингүй болгох
     *
     * @param merkleRoot  issue хийх үед үүссэн chainpoint баталгаан доторх merkleRoot утга
     * @param revokerName хүчингүй болгож буй хүний нэр
     * @param privateKey  нууц түлхүүр
     * @return Блокчэйний гүйлгээний ID
     */
    public String revoke(String merkleRoot, String revokerName, String privateKey) {
        Credentials wallet = Credentials.create(privateKey);
        return this.revoke(merkleRoot, revokerName, wallet);
    }

    /**
     * Бүртгэсэн нэг болон олон хашийг буцаан хүчингүй болгох
     *
     * @param merkleRoot   issue хийх үед үүссэн chainpoint баталгаан доторх merkleRoot утга
     * @param revokerName  хүчингүй болгож буй хүний нэр
     * @param keyStoreFile нууц түлхүүрийн encrypt хийгдсэн файлын зам
     * @param passphrase   нууц түлхүүрийг сэргээх код     * @return Блокчэйний гүйлгээний ID
     */
    public String revoke(String merkleRoot, String revokerName, String keyStoreFile, String passphrase)
            throws IOException, CipherException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revoke(merkleRoot, revokerName, wallet);
    }

    protected String revoke(String merkleRoot, String revokerName, Credentials wallet) {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        CertificationRegistration smartContract = createSmartContractInstance(web3j, wallet);
        String root = merkleRoot.toLowerCase();

        try {
            BigInteger creditBalance = smartContract.getCredit(this.issuerAddress).send();
            if (creditBalance.compareTo(BigInteger.ZERO) == 0)
                throw new InvalidCreditAmountException();

            CertificationRegistration.Certification cert = smartContract.getCertification(root).send();
            if (cert.id.compareTo(BigInteger.ZERO) == 0)
                throw new NotFoundException();

            TransactionReceipt tr = smartContract.revoke(root, revokerName).send();
            if (!tr.isStatusOK()) {
                throw new BlockchainNodeException();
            }
            return tr.getTransactionHash();
        } catch (AlreadyExistsException | InvalidCreditAmountException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BlockchainNodeException();
        }
    }

    private CertificationRegistration createSmartContractInstance(Web3j web3j, Credentials wallet) {
        if (!WalletUtils.isValidAddress(this.issuerAddress))
            throw new InvalidAddressException("Issuer wallet address is invalid.");

        if (!WalletUtils.isValidAddress(this.smartContractAddress))
            throw new InvalidAddressException("Smart contract address is invalid.");
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, wallet, this.chainId);
        CertificationRegistration smartContract = CertificationRegistration.load(this.smartContractAddress, web3j,
                transactionManager, gasProvider);
        try {
            smartContract.getCredit(this.issuerAddress).send();
        } catch (Exception ex) {
            throw new InvalidSmartContractException();
        }

        return smartContract;
    }

    private String getChainPointRoot(ArrayList<String> hashValues) throws NoSuchAlgorithmException {
        this.chainPointV2 = new ChainPointV2();
        chainPointV2.addLeaf(hashValues);
        chainPointV2.makeTree();

        return chainPointV2.getMerkleRoot();
    }
}
