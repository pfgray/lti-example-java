package net.paulgray.exampleltiapp.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pgray on 10/13/14.
 */
public class LtiProviderConfig {

    @JsonProperty
    public String default_base_url = "http://lti-provider.paulgray.net";
    @JsonProperty
    public String secure_base_url = "http://lti-provider.paulgray.net";
    @JsonProperty
    public String basic_lti_launch_path = "/lti";

    public LtiProviderConfig() {
    }

    public String getDefault_base_url() {
        return default_base_url;
    }

    public void setDefault_base_url(String default_base_url) {
        this.default_base_url = default_base_url;
    }

    public String getSecure_base_url() {
        return secure_base_url;
    }

    public void setSecure_base_url(String secure_base_url) {
        this.secure_base_url = secure_base_url;
    }

    public String getBasic_lti_launch_path() {
        return basic_lti_launch_path;
    }

    public void setBasic_lti_launch_path(String basic_lti_launch_path) {
        this.basic_lti_launch_path = basic_lti_launch_path;
    }
}
