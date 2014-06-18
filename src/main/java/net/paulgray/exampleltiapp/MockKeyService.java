package net.paulgray.exampleltiapp;

import org.imsglobal.aspect.LtiKeySecretService;

/**
 * Created by paul on 5/28/14.
 */
public class MockKeyService implements LtiKeySecretService {

    @Override
    public String getSecretForKey(String s) {
        return "secret";
    }
}
