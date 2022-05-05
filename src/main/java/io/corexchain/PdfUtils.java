package io.corexchain;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PdfUtils {
    PDDocument pdf;
    public PdfUtils(String sourceFilePath) throws IOException {
        File file = new File(sourceFilePath);
        this.pdf = PDDocument.load(file);
    }

    public void setMetaData(String key, String value) {
        COSDictionary object = (COSDictionary) pdf.getDocument().getTrailer().getDictionaryObject(COSName.INFO);
        object.setItem(key, new COSString(value));
    }

    public String calcHash(String hashType) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdf.save(byteArrayOutputStream);
        final MessageDigest digest = MessageDigest.getInstance(hashType);
        return new String(Hex.encode(digest.digest(byteArrayOutputStream.toByteArray())));
    }

    public void save(String destinationFilePath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdf.save(byteArrayOutputStream);
        FileOutputStream fos = new FileOutputStream(new File(destinationFilePath));
        byteArrayOutputStream.writeTo(fos);
    }

    public void close() throws IOException {
        pdf.close();
    }
}
