/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.paulgray.exampleltiapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.faces.render.ResponseStateManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.imsglobal.aspect.Lti;
import org.imsglobal.basiclti.LtiSigner;
import org.imsglobal.basiclti.LtiSigningException;
import org.imsglobal.basiclti.LtiVerificationResult;
import org.imsglobal.lti2.objects.consumer.ToolConsumer;
import org.imsglobal.lti2.objects.provider.ToolProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pgray
 */
@Controller
public class LtiController {

    private Map<String, ToolConsumerInfo> tool_consumer_profile_map = new HashMap<>();
    private SecureRandom random = new SecureRandom();

    private class ToolConsumerInfo {
        public String profileUrl;
        public String reg_key;
        public String reg_secret;

        private ToolConsumerInfo(String profileUrl, String reg_key, String reg_secret) {
            this.profileUrl = profileUrl;
            this.reg_key = reg_key;
            this.reg_secret = reg_secret;
        }
    }

    @Autowired
    LtiSigner signer;
    
    @Lti
    @RequestMapping(value = "/lti", method = RequestMethod.POST)
    public String ltiEntry(HttpServletRequest request, LtiVerificationResult result, HttpServletResponse resp, ModelMap map) throws Throwable{
        System.out.println("serving request...");
        if(!result.getSuccess()){
            resp.setStatus(HttpStatus.FORBIDDEN.value());
            return "error";
        } else {
            map.put("name", result.getLtiLaunchResult().getUser().getId());
            ObjectMapper mapper = new ObjectMapper();
            map.put("launch", mapper.writeValueAsString(result.getLtiLaunchResult()));
            return "lti";
        }
    }
    
    @RequestMapping(value = {"/register"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String ltiTest(@RequestParam Map params, ModelMap map) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String profileUrl = (String) params.get("tc_profile_url");
        String reg_key = (String) params.get("reg_key");
        String reg_secret = (String) params.get("reg_secret");
        //get the tcProfile url,

        //add it to a map with a randomly generated id in order for users to retrieve it after they're returned to the view
        String randomToken = nextRandomToken();
        tool_consumer_profile_map.put(randomToken, new ToolConsumerInfo(profileUrl, reg_key, reg_secret));
        map.put("tool_consumer_retrieval_token", randomToken);
        map.put("tool_proxy_registration_request", mapper.writeValueAsString(params));
        map.put("params", params);
        return "register";
    }

    @RequestMapping(value = {"/profile_retrieval"}, method = RequestMethod.GET)
    public ResponseEntity retrieveProfile(@RequestParam String token) throws IOException {
        String tc_profile_url = tool_consumer_profile_map.get(token).profileUrl;
        if(tc_profile_url == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //get the profile and return it.
        ToolConsumer tc = JsonReader.readJsonFromUrl(tc_profile_url, ToolConsumer.class);
        return new ResponseEntity(tc, HttpStatus.OK);
    }

    @RequestMapping(value = {"/toolRegistration"}, method = RequestMethod.POST)
    public ResponseEntity toolRegistration(@RequestBody JsonNode toolRegistrationDetails) throws Exception {

        HttpPost request = new HttpPost(toolRegistrationDetails.get("endpoint").asText());
        request.setEntity("");

        ToolConsumerInfo info = tool_consumer_profile_map.get(toolRegistrationDetails.get("token"));
        signer.sign(request, info.reg_key, info.reg_secret);

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        if(response.getStatusLine().getStatusCode() >= 400){
            throw new Exception("Got error from tool consumer");
        }

        return new ResponseEntity("Created tool proxy", HttpStatus.OK);
    }

    public String nextRandomToken() {
        return new BigInteger(130, random).toString(32);
    }

}
