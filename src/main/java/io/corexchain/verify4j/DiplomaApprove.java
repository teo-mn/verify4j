package io.corexchain.verify4j;

import io.corexchain.verify4j.chainpoint.MerkleTree;
import io.corexchain.verify4j.exceptions.*;
import io.nbs.contracts.UniversityDiploma;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class DiplomaApprove {
    protected StaticGasProvider gasProvider;

    protected static BigInteger GAS_PRICE = BigInteger.valueOf((long) (1e12));
    protected static BigInteger GAS_LIMIT = BigInteger.valueOf(2000000L);
    protected String smartContractAddress;
    protected String approverAddress;
    protected String nodeHost;

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    protected long chainId = 1104;

    /**
     *
     * @param smartContractAddress Ухаалаг гэрээний хаяг
     * @param approverAddress        илгээгч байгууллагын блокчэйний хаяг
     * @param nodeHost             блокчэйний нөүдний URL
     */
    public DiplomaApprove(String smartContractAddress, String approverAddress, String nodeHost) {
        this.smartContractAddress = Keys.toChecksumAddress(smartContractAddress);
        this.approverAddress = Keys.toChecksumAddress(approverAddress);
        this.nodeHost = nodeHost;
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
    }

    /**
     *
     * @param smartContractAddress Ухаалаг гэрээний хаяг
     * @param approverAddress        илгээгч байгууллагын блокчэйний хаяг
     * @param nodeHost             блокчэйний нөүдний URL
     * @param chainId              чэйн ID
     */
    public DiplomaApprove(String smartContractAddress, String approverAddress, String nodeHost, long chainId) {
        this.smartContractAddress = Keys.toChecksumAddress(smartContractAddress);
        this.approverAddress = Keys.toChecksumAddress(approverAddress);
        this.nodeHost = nodeHost;
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        this.chainId = chainId;
    }


    public boolean validateMetaData(String fileHash, String metaHash, DiplomaMetaDataDTO dto)
            throws NoSuchAlgorithmException {
        return validateMetaData(fileHash, metaHash, dto.convertToMap());
    }

    public boolean validateMetaData(String fileHash, String metaHash, Map<String, Object> metaData)
            throws NoSuchAlgorithmException {
        String jsonStr = JsonUtils.jsonMapToString(metaData);
        MerkleTree mk = new MerkleTree();
        String hashJson = mk.calcHash(jsonStr);
        return hashJson.equals(metaHash);
    }

    public String approve(String fileHash, String metaHash, DiplomaMetaDataDTO metaData, String privateKey) throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.approve(fileHash, metaHash, metaData.convertToMap(), wallet);
    }

    public String approve(String fileHash, String metaHash, DiplomaMetaDataDTO metaData, String keyStoreFile, String passphrase) throws NoSuchAlgorithmException, IOException, CipherException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.approve(fileHash, metaHash, metaData.convertToMap(), wallet);
    }

    public String approve(String fileHash, String metaHash, Map<String, Object> metaData, String privateKey) throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.approve(fileHash, metaHash, metaData, wallet);
    }

    public String approve(String fileHash, String metaHash, Map<String, Object> metaData, String keyStoreFile, String passphrase) throws NoSuchAlgorithmException, IOException, CipherException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.approve(fileHash, metaHash, metaData, wallet);
    }

    public String approve(String fileHash, String metaHash, Map<String, Object> metaData, Credentials wallet) throws NoSuchAlgorithmException, IOException {
        String jsonStr = JsonUtils.jsonMapToString(metaData);
        MerkleTree mk = new MerkleTree();
        String hashJson = mk.calcHash(jsonStr);
        if (!hashJson.equals(metaHash)) {
            throw new InvalidMetaDataException();
        }
        return approveUtil(fileHash, wallet);
    }

    private String approveUtil(String fileHash, Credentials wallet) {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));

        UniversityDiploma smartContract = createSmartContractInstance(web3j, wallet);
        try {
            BigInteger creditBalance = smartContract.getCredit(this.approverAddress).send();
            if (creditBalance.compareTo(BigInteger.ZERO) == 0)
                throw new InvalidCreditAmountException();
            UniversityDiploma.Certification cert = smartContract.getCertification(fileHash).send();
            if (cert.id.compareTo(BigInteger.ZERO) == 0)
                throw new NotFoundException();
            UniversityDiploma.ApproveInfo info = smartContract.getApproveInfo(fileHash).send();
            if (info.isApproved) {
                throw new AlreadyExistsException();
            }
            TransactionReceipt tr = smartContract.approve(fileHash).send();
            if (!tr.isStatusOK()) {
                throw new BlockchainNodeException();
            }
            return tr.getTransactionHash();
        } catch (InvalidCreditAmountException | NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            throw new BlockchainNodeException();
        }

    }

    private UniversityDiploma createSmartContractInstance(Web3j web3j, Credentials wallet) {
        if (!WalletUtils.isValidAddress(this.approverAddress))
            throw new InvalidAddressException("Issuer wallet address is invalid.");

        if (!WalletUtils.isValidAddress(this.smartContractAddress))
            throw new InvalidAddressException("Smart contract address is invalid.");
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, wallet, this.chainId);
        UniversityDiploma smartContract = UniversityDiploma.load(this.smartContractAddress, web3j,
                transactionManager, gasProvider);
        try {
            smartContract.getCredit(this.approverAddress).send();
        } catch (Exception ex) {
            throw new InvalidSmartContractException();
        }

        return smartContract;
    }
}
