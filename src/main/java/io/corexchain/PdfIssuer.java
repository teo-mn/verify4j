package io.corexchain;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.util.Date;

public class PdfIssuer extends Issuer {

    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost,
            long chainId) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, chainId);
    }

    public PdfIssuer(
            String smartContractAddress,
            String issuerAddress,
            String issuerName,
            String nodeHost) {
        super(smartContractAddress, issuerAddress, issuerName, nodeHost, 1104);
    }

    public String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String privateKey
    ) throws Exception {
        Credentials wallet = Credentials.create(privateKey);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, wallet);
    }

    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            String keyStoreFile,
            String passphrase
    ) throws Exception {
        Credentials wallet = WalletUtils.loadCredentials(passphrase, keyStoreFile);
        return this.issue(id, sourceFilePath, destinationFilePath, expireDate, desc, wallet);
    }

    private String issue(
            String id,
            String sourceFilePath,
            String destinationFilePath,
            Date expireDate,
            String desc,
            Credentials wallet
    ) throws Exception {



        return this.issue(id, "hash", expireDate, desc, wallet);
    }
}
