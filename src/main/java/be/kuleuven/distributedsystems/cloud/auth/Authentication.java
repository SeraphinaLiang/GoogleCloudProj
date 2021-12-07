package be.kuleuven.distributedsystems.cloud.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//RS256	--RSA256	RSASSA-PKCS1-v1_5 with SHA-256
public class Authentication {
    // public key : https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com
    RSAKey pubRSA;
    RSAKey privRSA;
    Algorithm algorithmRS;

    void init() {
        try {
            //Read a Public Key
            pubRSA = (RSAKey) PemUtils.readPublicKeyFromFile("rsa-public.pem", "RSA");
            //Read a Private Key
            privRSA = (RSAKey) PemUtils.readPrivateKeyFromFile("rsa-private.pem", "RSA");

            //RSA
            RSAPublicKey publicKey = (RSAPublicKey) pubRSA;
            RSAPrivateKey privateKey = (RSAPrivateKey) privRSA;
            algorithmRS = Algorithm.RSA256(publicKey, privateKey);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Create and Sign a Token
    String sign() {
        String token = "";
        try {
            token = JWT.create()
                    .withIssuer("auth0")
                    .sign(algorithmRS);

            System.out.println(token);
        } catch (JWTCreationException e) {
            e.printStackTrace();
        }
        return token;
    }

    //Verify a Token
    boolean verify(String token) {
        DecodedJWT jwt = null;
        boolean valid = true;
        try {
            JWTVerifier verifier = JWT.require(algorithmRS)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            System.out.println("Invalid signature or claims");
            valid = false;
        }
        return valid;
    }

    public static void main(String[] args) throws FirebaseAuthException {
        Authentication authentication = new Authentication();
        authentication.init();
        String token = authentication.sign();
        authentication.verify(token);

        // idToken comes from the client app (shown above)
        // check the source code
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();
    }
}
