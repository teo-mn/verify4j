package io.corexchain;

import io.corexchain.exceptions.InvalidAddressException;
import io.corexchain.exceptions.InvalidSmartContractException;
import io.nbc.contracts.Greeter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
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
import java.math.BigInteger;
import java.util.Date;

public class PdfIssuer {
    private String keyStoreFile;
    private String passPhrase;
    private String smartContractAddress;
    private String issuerAddress;
    private String issuerName;
    private String nodeHost;
    private Credentials wallet;
    private DefaultGasProvider gasProvider;

    public PdfIssuer(String keyStoreFile,
                     String passPhrase,
                     String smartContractAddress,
                     String issuerAddress,
                     String issuerName,
                     String nodeHost) throws CipherException, IOException {
        this.keyStoreFile = keyStoreFile;
        this.passPhrase = passPhrase;
        this.smartContractAddress = smartContractAddress;
        this.issuerAddress = issuerAddress;
        this.issuerName = issuerName;
        this.wallet = WalletUtils.loadCredentials(passPhrase, keyStoreFile);
//        this.wallet = Credentials.create("<privateKey>");
        DefaultGasProvider gasPrice = new DefaultGasProvider();
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

        Greeter smartContract = Greeter.load(this.smartContractAddress, web3j, this.wallet, gasProvider);
        if (!smartContract.isValid())
            throw new InvalidSmartContractException();

        smartContract.addCertification(hashValue, id, BigInteger.valueOf(expireDate.getTime()), "0", desc)
                .send().getTransactionHash();


        return null;
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
