package io.corexchain;

import io.nbc.contracts.CertificationRegistration;

public class VerifyResult {

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public CertificationRegistration.Certification getCert() {
        return cert;
    }

    public void setCert(CertificationRegistration.Certification cert) {
        this.cert = cert;
    }

    public CertificationRegistration.Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(CertificationRegistration.Issuer issuer) {
        this.issuer = issuer;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    private String state;
    private String metadata;
    private CertificationRegistration.Certification cert;
    private CertificationRegistration.Issuer issuer;

    public VerifyResult(CertificationRegistration.Certification cert, String state) {
        this.state = state;
        this.cert = cert;
        this.metadata = "";
    }

    public VerifyResult(CertificationRegistration.Certification cert,
                        CertificationRegistration.Issuer issuer, String state) {
        this.state = state;
        this.cert = cert;
        this.issuer = issuer;
        this.metadata = "";
    }
}
