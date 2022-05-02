package io.corexchain;

import io.corexchain.exceptions.InvalidAddressException;
import io.corexchain.exceptions.InvalidSmartContractException;
import io.nbc.contracts.CertificationRegistration;
import io.nbc.contracts.Greeter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Strings;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class PdfIssuer {
    private String smartContractAddress;
    private String issuerAddress;
    private String issuerName;
    private String nodeHost;
    private Credentials wallet;
    private StaticGasProvider gasProvider;

    public PdfIssuer(String privateKey,
                     String smartContractAddress,
                     String issuerAddress,
                     String issuerName,
                     String nodeHost){
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.nodeHost = nodeHost;
        this.wallet = Credentials.create(privateKey);
        this.gasProvider = new StaticGasProvider(BigInteger.valueOf(3000000L), BigInteger.valueOf(3000000L));
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
        this.gasProvider = new StaticGasProvider(BigInteger.valueOf(1000000L), BigInteger.valueOf(2000000L));
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


    private double getCreditBalance(String address) {
//        Web3j web3 = Web3j.build(new HttpService(this.nodeHost));
        return 0;
    }
//    private String transfer() {
//        TransactionReceipt transferReceipt = Transfer.sendFunds(
//                        web3j, credentials,
//                        "0x19e03255f667bdfd50a32722df860b1eeaf4d635",  // you can put any address here
//                        BigDecimal.ONE, Convert.Unit.WEI)  // 1 wei = 10^-18 Ether
//                .send();
//        log.info("Transaction complete, view it at https://rinkeby.etherscan.io/tx/"
//                + transferReceipt.getTransactionHash());
//
//    }
}
