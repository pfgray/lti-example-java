/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.paulgray.exampleltiapp;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.paulgray.exampleltiapp.config.LtiProviderConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.imsglobal.aspect.Lti;
import org.imsglobal.lti.launch.LtiSigner;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.imsglobal.lti.launch.LtiVerifier;
import org.imsglobal.lti2.objects.consumer.ToolConsumer;
import org.imsglobal.lti2.objects.provider.SecurityContract;
import org.imsglobal.lti2.objects.provider.ToolProfile;
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
import org.apache.commons.io.IOUtils;



/**
 *
 * @author pgray
 */
@Controller
public class LtiController {

    private Map<String, ToolConsumerInfo> tool_consumer_profile_map = new HashMap<>();
    private SecureRandom random = new SecureRandom();
    private LtiProviderConfig ltiConfig = new LtiProviderConfig();
    private final static Logger logger = Logger.getLogger(LtiController.class.getName());


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
        if(!result.getSuccess()){
            logger.info("Lti verification failed! error was: " + result.getError());
            logger.info("   message: " + result.getMessage());
            map.put("ltiError", result.getError().toString());
            resp.setStatus(HttpStatus.FORBIDDEN.value());
            return "error";
        } else {
            map.put("name", result.getLtiLaunchResult().getUser().getId());
            ObjectMapper mapper = new ObjectMapper();

            Map<String, String> params = new HashMap<>();
            for (String param: Collections.list(request.getParameterNames())) {
                params.put(param, request.getParameter(param));
            }
            map.put("params", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(params));

            map.put("launch", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.getLtiLaunchResult()));
            return "lti";
        }
    }

    @RequestMapping(value = {"/register"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String ltiTest(@RequestParam Map params, ModelMap map) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String profileUrl = (String) params.get("tc_profile_url");
        String reg_key = (String) params.get("reg_key");
        String reg_password = (String) params.get("reg_password");
        //get the tcProfile url,

        //add it to a map with a randomly generated id in order for users to retrieve it after they're returned to the view
        String randomToken = nextRandomToken();
        tool_consumer_profile_map.put(randomToken, new ToolConsumerInfo(profileUrl, reg_key, reg_password));
        map.put("tool_consumer_retrieval_token", randomToken);
        map.put("tool_proxy_registration_request", mapper.writeValueAsString(params));
        map.put("params", params);
        return "register";
    }

    @RequestMapping(value = {"/profile_retrieval"}, method = RequestMethod.GET)
    public ResponseEntity retrieveProfile(@RequestParam String token) throws IOException {
        String tc_profile_url = tool_consumer_profile_map.get(token).profileUrl;
        logger.info("******Got ");
        if(tc_profile_url == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        //get the profile and return it.
        ToolConsumer tc = JsonReader.readJsonFromUrl(tc_profile_url, ToolConsumer.class);
        return new ResponseEntity(tc, HttpStatus.OK);
    }

    @RequestMapping(value = {"/toolRegistration"}, method = RequestMethod.POST)
    public ResponseEntity toolRegistration(@RequestBody JsonNode toolRegistrationDetails, @RequestParam String token) throws Exception {
        ToolProxy tp = new ToolProxy();
        tp.setContext(ToolProxy.CONTEXT_URL);
        tp.setTool_proxy_guid("guid");
        tp.setId("id");
        tp.setType("ToolProxy");
        tp.setTool_profile(getToolProfile());
        tp.setSecurity_contract(getSecurityContract());

        ObjectMapper mapper = new ObjectMapper();
        HttpPost request = new HttpPost(toolRegistrationDetails.get("endpoint").asText());
        request.setHeader("Content-type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(tp)));

        ToolConsumerInfo info = tool_consumer_profile_map.get(token);
        signer.sign(request, info.reg_key, info.reg_secret);

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        if(response.getStatusLine().getStatusCode() >= 400){
            StringWriter writer = new StringWriter();
            IOUtils.copy(response.getEntity().getContent(), writer, "utf-8");
            String theString = writer.toString();
            throw new Exception("Got error from tool consumer: " + response.getStatusLine().getStatusCode() + " - " + theString);
        }

        return new ResponseEntity("Created tool proxy", HttpStatus.OK);
    }

    public String nextRandomToken() {
        return new BigInteger(130, random).toString(32);
    }

    private ToolProfile getToolProfile(){
        ToolProfile tp = new ToolProfile();
        tp.setBase_url_choice(getBaseUrlChoices());
        tp.setResource_handler(getResourceHandler());
        return tp;
    }

    private ArrayNode getBaseUrlChoices(){
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode choices = mapper.createArrayNode();

        ObjectNode baseUrlChoice = mapper.createObjectNode();
        baseUrlChoice.put("default_base_url", ltiConfig.default_base_url);
        baseUrlChoice.put("secure_base_url", ltiConfig.secure_base_url);

        baseUrlChoice.put("selector", "DefaultSelector");

        choices.add(baseUrlChoice);
        return choices;
    }

    private ArrayNode getResourceHandler() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode resourceHandler = mapper.createArrayNode();

        ObjectNode resource = mapper.createObjectNode();

        ArrayNode messageList = mapper.createArrayNode();
        ObjectNode message = mapper.createObjectNode();
        message.put("message_type", ToolConsumer.LtiCapability.BASICLTI_LAUNCH);
        message.put("path", ltiConfig.basic_lti_launch_path);
        ArrayNode parameterList = mapper.createArrayNode();
        ObjectNode parameter = mapper.createObjectNode();
        parameter.put("name", "my_custom_course_offering_title_parameter");
        parameter.put("variable", ToolConsumer.LtiCapability.CO_TITLE);
        parameterList.add(parameter);
        message.put("parameter", parameterList);
        messageList.add(message);

        resource.put("message", messageList);
        resource.put("name", getValue("Lti Example App"));

        resourceHandler.add(resource);
        return resourceHandler;
    }

    private ObjectNode getValue(String value){
        ObjectNode valueNode = new ObjectMapper().createObjectNode();
        valueNode.put("default_value", value);
        return valueNode;
    }

    private SecurityContract getSecurityContract(){
        SecurityContract contract = new SecurityContract();
        contract.setShared_secret("secret");
        return contract;
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseEntity getConfig() {
        return new ResponseEntity(ltiConfig, HttpStatus.OK);
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ResponseEntity editConfig(@RequestBody LtiProviderConfig config) {
        ltiConfig = config;
        return new ResponseEntity(ltiConfig, HttpStatus.OK);
    }


}
