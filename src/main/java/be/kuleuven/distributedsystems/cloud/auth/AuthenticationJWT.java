package be.kuleuven.distributedsystems.cloud.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//RS256	--RSA256	RSASSA-PKCS1-v1_5 with SHA-256
public class AuthenticationJWT {
    RSAKey pubRSA;
    RSAKey privRSA;
    Algorithm algorithmRS;

    void init() {
        try {
            //Read a Public Key
            pubRSA = (RSAKey) PemUtils.readPublicKeyFromFile("", "RSA");
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

    //verify the header, payload, and signature of the ID token.
    boolean verify(String token) {
        //decode the token
        DecodedJWT jwt = JWT.decode(token);

        // ID Token Header Claims
        /**
         * alg	Algorithm	"RS256"
         * kid	Key ID	Must correspond to one of the public keys listed at
         * https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com
         */
        String alg = jwt.getAlgorithm();
        String kid = jwt.getKeyId();

        if (!alg.equals("RS256")) {
            return false;
        }
        //TODO
        if (!(kid.equals("PK1") || kid.equals("PK2"))) {
            return false;
        }

        // ID Token Payload Claims
        /**
         * exp	Expiration time	Must be in the future. The time is measured in seconds since the UNIX epoch.
         * iat	Issued-at time	Must be in the past. The time is measured in seconds since the UNIX epoch.
         * aud	Audience	Must be your Firebase project ID, the unique identifier for your Firebase project, which can be found in the URL of that project's console.
         * iss	Issuer	Must be "https://securetoken.google.com/<projectId>", where <projectId> is the same project ID used for aud above.
         * sub	Subject	Must be a non-empty string and must be the uid of the user or device.
         * auth_time	AuthenticationJWT time	Must be in the past. The time when the user authenticated.
         */
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
        /**
         * Finally, ensure that the ID token was signed by the private key corresponding to the token's kid claim.
         * Grab the public key from https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com
         * and use a JWT library to verify the signature.
         * Use the value of max-age in the Cache-Control header of the response from that endpoint to know when to refresh the public keys.
         *
         * If all the above verifications are successful,
         * you can use the subject (sub) of the ID token as the uid of the corresponding user or device.
         */





        try {
            JWTVerifier verifier = JWT.require(algorithmRS)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            System.out.println("Invalid signature or claims");
        }

        return true;
    }

    private List<String> getPublicKeyFromGoogle(){
        ArrayList<String> PKs = new ArrayList<>();



        return PKs;
    }


    public static void main(String[] args) throws FirebaseAuthException {
        AuthenticationJWT authentication = new AuthenticationJWT();
        authentication.init();
        String token = authentication.sign();
        authentication.verify(token);

        // idToken comes from the client app (shown above)
        // check the source code
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();
    }
}
