package be.kuleuven.distributedsystems.cloud.auth;

import net.sf.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class SendHttps {
    public static Long responseTime;
    public static Integer maxAge;

    public HashMap<String, String> getPublicKeys()  {

        HashMap<String, String> publicKeys = new HashMap<>();

        // request
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        RestTemplate restTemplate =restTemplateConfig.restTemplate();

        String urls = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
        URI uri = URI.create(urls);

        MultiValueMap<String, String> httpHeaders = new LinkedMultiValueMap<>();
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("Accept-Encoding", "gzip");
        httpHeaders.add("Connection", "keep-alive");
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");

        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);

        // response
        if (response.getStatusCode() == HttpStatus.OK) {
            // time
            responseTime = (System.currentTimeMillis() / 1000);
            String[] cacheControl = Objects.requireNonNull(response.getHeaders().getCacheControl()).split(",");
            maxAge = Integer.parseInt((cacheControl[1].split("="))[1]);
            // public keys
            String res = response.getBody();
            JSONObject obj = JSONObject.fromObject(res);
            for (Object o : obj.keySet()){
                String id = o.toString();
                String pk = obj.getString(id);
                System.out.println(pk);
                publicKeys.put(id,pk);
            }
        }

        return publicKeys;
    }

}
