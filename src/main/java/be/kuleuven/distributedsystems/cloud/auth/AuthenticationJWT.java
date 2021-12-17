package be.kuleuven.distributedsystems.cloud.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//RS256	--RSA256	RSASSA-PKCS1-v1_5 with SHA-256
public class AuthenticationJWT {

    PublicKey pubRSA;
    PrivateKey privRSA;
    Algorithm algorithmRS;

    boolean init = false;
    HashMap<String, String> publicKeys;
    SendHttps sendHttps = new SendHttps();
    String IDtoken = "";

    //verify the header, payload, and signature of the ID token.
    boolean verify(String token) {
        try {
            this.IDtoken = token;
            getPublicKeyFromGoogle();
            DecodedJWT jwt = JWT.decode(token);

            // ID Token Header Claims
            String alg = jwt.getAlgorithm();
            String kid = jwt.getKeyId();

            // alg
            if (!alg.equals("RS256")) {
                return false;
            }
            // kid
            boolean sameKID = false;
            for (String id : publicKeys.keySet()) {
                String pk = publicKeys.get(id);
                if (kid.equals(id)) {
                    pubRSA = PemUtils.string2publicKey(pk);
                    sameKID = true;
                    break;
                }
            }
            if (!sameKID) {
                return false;
            }

            // ID Token Payload Claims
            Map<String, Claim> payloads = jwt.getClaims();
            Integer exp = payloads.get("exp").asInt();
            Integer iat = payloads.get("iat").asInt();
            String aud = payloads.get("aud").asString();
            String iss = payloads.get("iss").asString();
            String sub = payloads.get("sub").asString();
            Integer auth_time = payloads.get("auth_time").asInt();

            String user_id = payloads.get("user_id").asString();

            if (exp <= (System.currentTimeMillis() / 1000)) {
                return false;
            }
            if (iat >= (System.currentTimeMillis() / 1000)) {
                return false;
            }
            if (!aud.equals("true-bit-333719")) {
                return false;
            }
            if (!iss.equals("https://securetoken.google.com/true-bit-333719")) {
                return false;
            }
            if (sub.isEmpty() || (!sub.equals(user_id))) {
                return false;
            }
            if (auth_time >= (System.currentTimeMillis() / 1000)) {
                return false;
            }

            // signature
            if (!verifySignature()) {
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void getPublicKeyFromGoogle() {
        // get first key
        if (!init) {
            publicKeys = sendHttps.getPublicKeys();
            init = true;
        }

        // check time if expires
        long currentTime = (System.currentTimeMillis() / 1000);
        if ((currentTime - SendHttps.responseTime) > SendHttps.maxAge) {
            publicKeys = sendHttps.getPublicKeys();
        }
    }

    private boolean verifySignature() {

        DecodedJWT jwt = null;
        try {

            algorithmRS = Algorithm.RSA256((RSAPublicKey) pubRSA,null);
            JWTVerifier verifier = JWT.require(algorithmRS)
                    .withIssuer("auth0")
                    .build();
            jwt = verifier.verify(IDtoken);

        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
