package io.corexchain;

import io.corexchain.exceptions.*;
import io.nbc.contracts.CertificationRegistration;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
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

    public Issuer(String smartContractAddress,
                  String issuerAddress,
                  String issuerName,
                  String nodeHost,
                  long chainId) {
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        this.chainId = chainId;
    }

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

    public Tuple2<String, String> issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc,
            String privateKey
    ) throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, hashValue, expireDate, desc, wallet);
    }

    protected Tuple2<String, String> issue(String id,
                                           String hashValue,
                                           Date expireDate,
                                           String desc,
                                           Credentials wallet) throws NoSuchAlgorithmException, IOException {

        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        CertificationRegistration smartContract = createSmartContractInstance(web3j, wallet);
        String root = getChainPointRoot(hashValue);

        try {
            BigInteger creditBalance = smartContract.getCredit(this.issuerAddress).send();
            if (creditBalance.compareTo(BigInteger.ZERO) == 0)
                throw new InvalidCreditAmountException();

            CertificationRegistration.Certification cert = smartContract.getCertification(root).send();
            if (cert.id.compareTo(BigInteger.ZERO) != 0)
                throw new AlreadyExistsException();

            BigInteger exDate = expireDate != null ? BigInteger.valueOf(expireDate.getTime()) : BigInteger.ZERO;
            TransactionReceipt tr = smartContract.addCertification(root, id, exDate, "0", desc).send();
            if (!tr.isStatusOK()) {
                throw new BlockchainNodeException();
            }
            return new Tuple2<>(tr.getTransactionHash(), chainPointV2.getReceipt(0, tr.getTransactionHash(),
                    this.chainId != 1104));
        } catch (AlreadyExistsException | InvalidCreditAmountException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BlockchainNodeException();
        }
    }

    public String revoke(String hashValue, String revokerName, String privateKey)
            throws NoSuchAlgorithmException, IOException {
        Credentials wallet = Credentials.create(privateKey);
        return this.revoke(hashValue, revokerName, wallet);
    }

    public String revoke(String hashValue, String revokerName, String keyStoreFile, String passphrase)
            throws IOException, CipherException, NoSuchAlgorithmException {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.revoke(hashValue, revokerName, wallet);
    }

    protected String revoke(String hashValue, String revokerName, Credentials wallet)
            throws NoSuchAlgorithmException, IOException {
        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));
        CertificationRegistration smartContract = createSmartContractInstance(web3j, wallet);
        String root = getChainPointRoot(hashValue);

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

    private CertificationRegistration createSmartContractInstance(Web3j web3j, Credentials wallet) throws IOException {
        if (!WalletUtils.isValidAddress(this.issuerAddress))
            throw new InvalidAddressException("Issuer wallet address is invalid.");

        if (!WalletUtils.isValidAddress(this.smartContractAddress))
            throw new InvalidAddressException("Smart contract address is invalid.");
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, wallet, this.chainId);
        CertificationRegistration smartContract = CertificationRegistration.load(this.smartContractAddress, web3j,
                transactionManager, gasProvider);
        // TODO: Сүүгий энийг давдаг болгох
//        if (!smartContract.isValid())
//            throw new InvalidSmartContractException();

        return smartContract;
    }

    private String getChainPointRoot(String hashValue) throws NoSuchAlgorithmException {
        this.chainPointV2 = new ChainPointV2();
        chainPointV2.addLeaf(new ArrayList<>(Collections.singleton(hashValue)));
        chainPointV2.makeTree();

        return chainPointV2.getMerkleRoot();
    }
}
