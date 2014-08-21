/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.paulgray.exampleltiapp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.imsglobal.aspect.Lti;
import org.imsglobal.basiclti.LtiVerificationResult;
import org.imsglobal.lti2.objects.ToolConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pgray
 */
@Controller
public class LtiController {

    private Map<String, String> tool_consumer_profile_map = new HashMap<>();
    private SecureRandom random = new SecureRandom();
    
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
        String tcProfileUrl = (String) params.get("tc_profile_url");
        //get the tcProfile url,

        //add it to a map with a randomly generated id in order for users to retrieve it after they're returned to the view
        String randomToken = nextRandomToken();
        tool_consumer_profile_map.put(randomToken, tcProfileUrl);
        map.put("tool_consumer_retrieval_token", randomToken);
        map.put("tool_proxy_registration_request", mapper.writeValueAsString(params));
        map.put("params", params);
        return "register";
    }

    @RequestMapping(value = {"/profile_retrieval"}, method = RequestMethod.GET)
    public ResponseEntity retrieveProfile(@RequestParam String token) throws IOException {
        String tc_profile_url = tool_consumer_profile_map.remove(token);
        if(tc_profile_url == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //get the profile and return it.
        ToolConsumer tc = JsonReader.readJsonFromUrl(tc_profile_url, ToolConsumer.class);
        return new ResponseEntity(tc, HttpStatus.OK);
    }

    public String nextRandomToken() {
        return new BigInteger(130, random).toString(32);
    }

}
