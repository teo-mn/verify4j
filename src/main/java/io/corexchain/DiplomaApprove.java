package io.corexchain;

import io.corexchain.chainpoint.MerkleTree;
import io.corexchain.exceptions.*;
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


    public boolean validateMetaData(String fileHash, String imageHash, DiplomaMetaDataDTO dto)
            throws NoSuchAlgorithmException {
        return validateMetaData(fileHash, imageHash, dto.convertToMap());
    }

    public boolean validateMetaData(String fileHash, String imageHash, Map<String, String> metaData)
            throws NoSuchAlgorithmException {
        String jsonStr = JsonUtils.jsonMapToString(metaData);
        System.out.println(jsonStr);
        MerkleTree mk = new MerkleTree();
        String hashJson = mk.calcHash(jsonStr);
        System.out.println(hashJson);
        String mergedHash = mk.mergeHash(imageHash, hashJson);
        System.out.println(mergedHash);
        return fileHash.equals(mergedHash);
    }

    public String approve(String fileHash, String imageHash, DiplomaMetaDataDTO metaData, String privateKey) throws NoSuchAlgorithmException {
        Credentials wallet = Credentials.create(privateKey);
        return this.approve(fileHash, imageHash, metaData.convertToMap(), wallet);
    }

    public String approve(String fileHash, String imageHash, DiplomaMetaDataDTO metaData, String keyStoreFile, String passphrase) throws NoSuchAlgorithmException, IOException, CipherException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.approve(fileHash, imageHash, metaData.convertToMap(), wallet);
    }

    public String approve(String fileHash, String imageHash, Map<String, String> metaData, String privateKey) throws NoSuchAlgorithmException {
        Credentials wallet = Credentials.create(privateKey);
        return this.approve(fileHash, imageHash, metaData, wallet);
    }

    public String approve(String fileHash, String imageHash, Map<String, String> metaData, String keyStoreFile, String passphrase) throws NoSuchAlgorithmException, IOException, CipherException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.approve(fileHash, imageHash, metaData, wallet);
    }

    public String approve(String fileHash, String imageHash, Map<String, String> metaData, Credentials wallet) throws NoSuchAlgorithmException {
        String jsonStr = JsonUtils.jsonMapToString(metaData);
        MerkleTree mk = new MerkleTree();
        String hashJson = mk.calcHash(jsonStr);
        String mergedHash = mk.mergeHash(imageHash, hashJson);
        if (!fileHash.equals(mergedHash)) {
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
            TransactionReceipt tr = smartContract.approve(fileHash).send();
            if (!tr.isStatusOK()) {
                throw new BlockchainNodeException();
            }
            return tr.getTransactionHash();
        } catch (InvalidCreditAmountException | NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
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
