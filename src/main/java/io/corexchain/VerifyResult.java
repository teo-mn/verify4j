package io.corexchain;

import io.nbc.contracts.CertificationRegistration;

public class VerifyResult{
    public String issuerName;
    public String state;
    public CertificationRegistration.Certification cert;

    public VerifyResult(CertificationRegistration.Certification cert, String issuerName, String state) {
        this.issuerName = issuerName;
        this.state = state;
        this.cert = cert;
    }
}
