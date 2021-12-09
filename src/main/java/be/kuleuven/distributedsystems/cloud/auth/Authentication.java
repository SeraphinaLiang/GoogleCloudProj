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

    //verify the header, payload, and signature of the ID token.
    boolean verify(String token) {
        // ID Token Header Claims
        /**
         * alg	Algorithm	"RS256"
         * kid	Key ID	Must correspond to one of the public keys listed at https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com
         */

        // ID Token Payload Claims
        /**
         * exp	Expiration time	Must be in the future. The time is measured in seconds since the UNIX epoch.
         * iat	Issued-at time	Must be in the past. The time is measured in seconds since the UNIX epoch.
         * aud	Audience	Must be your Firebase project ID, the unique identifier for your Firebase project, which can be found in the URL of that project's console.
         * iss	Issuer	Must be "https://securetoken.google.com/<projectId>", where <projectId> is the same project ID used for aud above.
         * sub	Subject	Must be a non-empty string and must be the uid of the user or device.
         * auth_time	Authentication time	Must be in the past. The time when the user authenticated.
         */

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
