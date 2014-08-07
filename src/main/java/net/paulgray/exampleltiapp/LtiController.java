/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.paulgray.exampleltiapp;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.imsglobal.aspect.Lti;
import org.imsglobal.basiclti.LtiVerificationResult;
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
    
    @Lti
    @RequestMapping(value = "/lti", method = RequestMethod.POST)
    public String ltiEntry(HttpServletRequest request, LtiVerificationResult result, HttpServletResponse resp, ModelMap map) throws Throwable{
        System.out.println("serving request...");
        if(!result.getSuccess()){
            resp.setStatus(HttpStatus.SC_FORBIDDEN);
            return "error";
        } else {
            map.put("name", result.getLtiLaunchResult().getUser().getId());
            ObjectMapper mapper = new ObjectMapper();
            map.put("launch", mapper.writeValueAsString(result.getLtiLaunchResult()));
            return "lti";
        }
    }
    
    @RequestMapping(value = {"/register"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String ltiTest(@RequestParam Map params, ModelMap map){
        String tcProfileUrl = (String) params.get("tc_profile_url");
        
        map.put("params", params);
        return "register";
    }

}
