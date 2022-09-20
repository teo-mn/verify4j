package io.corexchain.verify4j;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public COSBase getMetaData(String key) {
        COSDictionary object = (COSDictionary) pdf.getDocument().getTrailer().getDictionaryObject(COSName.INFO);
        return object.getItem(key);
    }

    public String calcHash(String hashType) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdf.save(byteArrayOutputStream);
        final MessageDigest digest = MessageDigest.getInstance(hashType);
        return new String(Hex.encode(digest.digest(byteArrayOutputStream.toByteArray()))).toLowerCase();
    }

    public static String calcHash(String filePath, String hashType) throws IOException, NoSuchAlgorithmException {
        InputStream is = Files.newInputStream(Paths.get(filePath));
        final MessageDigest digest = MessageDigest.getInstance(hashType);
        return new String(Hex.encode(digest.digest(is.readAllBytes()))).toLowerCase();
    }

    public void save(String destinationFilePath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdf.save(byteArrayOutputStream);
        FileOutputStream fos = new FileOutputStream(new File(destinationFilePath));
        byteArrayOutputStream.writeTo(fos);
        fos.close();
        byteArrayOutputStream.close();
    }

    public void close() throws IOException {
        pdf.close();
    }
}
