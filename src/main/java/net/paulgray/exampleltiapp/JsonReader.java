package net.paulgray.exampleltiapp;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.imsglobal.lti2.objects.consumer.ToolConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonReader {

    public static <T> T readJsonFromUrl(String url, Class<T> claz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        InputStream is = new URL(url).openStream();
        try {
            return mapper.readValue(is, claz);
        } finally {
            is.close();
        }
    }

}