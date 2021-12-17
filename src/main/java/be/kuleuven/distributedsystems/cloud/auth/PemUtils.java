package be.kuleuven.distributedsystems.cloud.auth;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class PemUtils {


    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(keyBytes));
            publicKey = cer.getPublicKey();
//            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e){
            e.printStackTrace();
        }

        return publicKey;
    }

    public static PublicKey string2publicKey(String s){
        return getPublicKey(Base64.getDecoder().decode(s.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "").replaceAll("\n", "").getBytes()),"RSA");
    }

}
