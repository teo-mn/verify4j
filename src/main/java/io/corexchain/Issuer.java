package io.corexchain;

import io.corexchain.exceptions.AlreadyExistsException;
import io.corexchain.exceptions.InvalidAddressException;
import io.corexchain.exceptions.InvalidCreditAmountException;
import io.nbc.contracts.CertificationRegistration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.Date;

public class Issuer {
    protected StaticGasProvider gasProvider;

    protected static BigInteger GAS_PRICE = BigInteger.valueOf((long) (1e12));
    protected static BigInteger GAS_LIMIT = BigInteger.valueOf(2000000L);
    protected String smartContractAddress;
    protected String issuerAddress;
    protected String issuerName;
    protected String nodeHost;

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    protected long chainId = 1104;

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

    public String issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc,
            String keyStoreFile,
            String passphrase
    ) throws Exception {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, hashValue, expireDate, desc, wallet);
    }

    public String issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc,
            String privateKey
    ) throws Exception {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, hashValue, expireDate, desc, wallet);
    }

    protected String issue(String id,
                         String hashValue,
                         Date expireDate,
                         String desc,
                         Credentials wallet) throws Exception {
        if (!WalletUtils.isValidAddress(this.issuerAddress))
            throw new InvalidAddressException("Issuer wallet address is invalid.");

        if (!WalletUtils.isValidAddress(this.smartContractAddress))
            throw new InvalidAddressException("Smart contract address is invalid.");

        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));

        RawTransactionManager transactionManager = new RawTransactionManager(web3j, wallet, this.chainId);
        CertificationRegistration smartContract = CertificationRegistration.load(this.smartContractAddress, web3j, transactionManager, gasProvider);
        // TODO: Сүүгий энийг давдаг болгох
//        if (!smartContract.isValid())
//            throw new InvalidSmartContractException();

        BigInteger creditBalance = smartContract.getCredit(this.issuerAddress).send();
        if (creditBalance.compareTo(BigInteger.ZERO) == 0)
            throw new InvalidCreditAmountException();

        CertificationRegistration.Certification cert = smartContract.getCertification(hashValue).send();
        if (cert.id.compareTo(BigInteger.ZERO) != 0)
            throw new AlreadyExistsException();

        BigInteger exDate = expireDate != null ? BigInteger.valueOf(expireDate.getTime()) : BigInteger.ZERO;
        TransactionReceipt tr = smartContract.addCertification(hashValue, id, exDate, "0", desc).send();
        return tr.getTransactionHash();
    }
}
