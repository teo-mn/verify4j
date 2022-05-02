package io.corexchain;

import io.corexchain.exceptions.AlreadyExistsException;
import io.corexchain.exceptions.InvalidAddressException;
import io.corexchain.exceptions.InvalidCreditAmountException;
import io.nbc.contracts.CertificationRegistration;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

public class PdfIssuer {
    private String smartContractAddress;
    private String issuerAddress;
    private String issuerName;
    private String nodeHost;
    private Credentials wallet;
    private StaticGasProvider gasProvider;

    private static BigInteger GAS_PRICE = BigInteger.valueOf(3000000L);
    private static BigInteger GAS_LIMIT = BigInteger.valueOf(3000000L);

    public PdfIssuer(String privateKey,
                     String smartContractAddress,
                     String issuerAddress,
                     String issuerName,
                     String nodeHost) {
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.wallet = Credentials.create(privateKey);
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
    }

    public PdfIssuer(String keyStoreFile,
                     String passPhrase,
                     String smartContractAddress,
                     String issuerAddress,
                     String issuerName,
                     String nodeHost) throws CipherException, IOException {
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.wallet = WalletUtils.loadCredentials(passPhrase, keyStoreFile);
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
    }

    public String issue(
            String id,
            String hashValue,
            Date expireDate,
            String desc
    ) throws Exception {
        if (!WalletUtils.isValidAddress(this.issuerAddress))
            throw new InvalidAddressException("Issuer wallet address is invalid.");

        if (!WalletUtils.isValidAddress(this.smartContractAddress))
            throw new InvalidAddressException("Smart contract address is invalid.");

        Web3j web3j = Web3j.build(new HttpService(this.nodeHost));

        RawTransactionManager transactionManager = new RawTransactionManager(web3j, this.wallet, 3305);
        CertificationRegistration smartContract = CertificationRegistration.load(this.smartContractAddress, web3j, transactionManager, gasProvider);
        // TODO: Сүүгий энийг давдаг болгох
//        if (!smartContract.isValid())
//            throw new InvalidSmartContractException();

        // TODO: Сүүгий энийг зөв ажилдаг болгох
        BigInteger creditBalance = smartContract.getCredit(this.issuerAddress).send();
        if (creditBalance.compareTo(BigInteger.ZERO) == 0)
            throw new InvalidCreditAmountException();

        // TODO: Сүүгий энийг зөв ажилдаг болгох
        CertificationRegistration.Certification cert = smartContract.getCertification(hashValue).send();
        if (cert.id.compareTo(BigInteger.ZERO) != 0)
            throw new AlreadyExistsException();

        BigInteger exDate = expireDate != null ? BigInteger.valueOf(expireDate.getTime()) : BigInteger.ZERO;
        // TODO: Fix: java.lang.RuntimeException: Error processing transaction request: transaction underpriced
        TransactionReceipt tr = smartContract.addCertification(hashValue, id, exDate, "0", desc).send();
        return tr.getTransactionHash();
    }

    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc
    ) {
        return null;
    }
}
