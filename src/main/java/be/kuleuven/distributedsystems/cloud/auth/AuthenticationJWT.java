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
            String pri =
                    "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLetkE0HWaNQ6j" +
                    "+xrku9Vdr4j2akTkU3e2Qk5XH98Mztq2qnjmSM3Aayor/E+50b7r+H5dngAf+xcm" +
                    "Ydjw6FD9JGpRUKNyBLaKsaIj/SHxn7E2LL9RNjHGnYzAVX291JtGlissQeoTEqmX" +
                    "9yb9ohfLOpaPp1Ymz2bx/PfHWKeA+vI9bxEWMgG38DZuSL6fhasUoI7YWEDGNFp5" +
                    "M7Y8hI8gCWiquNNFfcnZrcI6HOLoPqkMxc5LB8ImuC+NxniPRkJv4ouQABx4ICoo" +
                    "RGt8yvDJsSNEcM47fvkJ9VUrGTOQOC6Vbv3zQA9nVbywf5jL7xAST7NxFBfRVmqY" +
                    "NOTPILNBAgMBAAECggEAQUx9q4lYTDH9rksNkNzkdom36tgknp9YmgFCUbxVIK+b" +
                    "SaaAYOp5OOhLIs14KlowqlpW9yUWxiyWe8dHztvG7c3LXqgBo7v2dqDVtzXrODa2" +
                    "sHuAtAevxpzVx/1Hem1pnSfg1/WZMCC7kxyKAzgK6bm2Coi5YYAKedrkCzGco4uS" +
                    "P29JR9xOW0rykPSbXrCegs32SfO29UoJ8U4ToLw1Qmybz5qy02u5IJ8q7vZgPMQq" +
                    "5IQg/NpmcOIUa07TaGSSzpvG251dIImLILlKU+3UNeskrIFTe7X4l2drzfvePRmI" +
                    "w/uHz2SS9Ztc9GWlWtCLpWLp6QV+zmVj2RC0AvwHPwKBgQDpD12aKB1JiDZbG3LM" +
                    "7d+hrjIWcppym1nuyjde6CU8cOr4YUuHGyzfyzT+B8j1lwEIveCda22NHKwi+SFr" +
                    "UUCEoJMUXyi1bxNxo+vfax0rDjJLL82WHgZoAWBzqrCkMWkky0Mi7G/7WtKY6VD/" +
                    "eschirRj3a2PWpxtg5wH58lSxwKBgQDfgh+Aqn5fJstCNd6seRcrQIJiBqPM7QAH" +
                    "rCMd1h42v0kDoQGE+9eJ1kf8TYdrTd01SB8FZP40V4wdjmEPqXFGQ6U2k212+FEg" +
                    "anA1lTEN3KkUngmePVURlGp1SSBPI1BPD8ZH65Kif4YMVMlK5FNNReNd5B8p0eyq" +
                    "JtSabD5BtwKBgGBWG6jnQAPvLBjI5dFT2obojIe+45zQHoKYT/8JEqtDOb1l6mR0" +
                    "lT4Er2j6KtVpj+HfKwOnLmeQHI9wT6Ieuf6YqXNYSmmE+pKU4aE+k5YCjkOKBP8y" +
                    "dg9z0jy9p7qXOhEdfCjpdvh9eGQAvZx9UebJgPtu3JlPKS4TouGZrBxtAoGBAI38" +
                    "WsjhBlCqE1unXyLP9gD7BRkCwNHxCSDWoKRWnnEHCXotsQpq9lzQ7IACPHHVUB6G" +
                    "B3bk1nwn1ZH9HfnWGWialnzaISL/0oG2PDw936C/ugWn89I7giwdzZdechD0DRN8" +
                    "oOiVZVyniF+TCSDzdVvUdwDxZz6o5iCddFf5RX93AoGAA78D5AIjhypHx6yuTE7Q" +
                    "PLQU0KiDyNz7yL4C/1s4YBjGnR07KGReTkgT6Xbd9jdRYUtjoOsb53I3J0aLRrZG" +
                    "plryDvxNR/z6XfBZOVjZpCx3xub3kG95PLT85fKNCoe7MVK0rvV8aOK4LNwOzdwV" +
                    "VFpPThIFcwCrvULh9yXWDxc=";
            KeyFactory kf = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pri.getBytes()));
            privRSA = kf.generatePrivate(keySpec);
            //privRSA = PemUtils.readPrivateKeyFromFile("src/main/resources/rsa-private.pem", "RSA");
            algorithmRS = Algorithm.RSA256((RSAPublicKey) pubRSA, (RSAPrivateKey) privRSA);

            JWTVerifier verifier = JWT.require(algorithmRS)
                    .withIssuer("auth0")
                    .build();

            jwt = verifier.verify(IDtoken);

        } catch (JWTVerificationException e) {
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (InvalidKeySpecException e){
            e.printStackTrace();
        }

        return true;
    }
}
