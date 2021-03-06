package org.radarcns.auth.config;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.radarcns.auth.token.validation.ECTokenValidationAlgorithm;
import org.radarcns.auth.token.validation.RSATokenValidationAlgorithm;


/**
 * Created by dverbeec on 19/06/2017.
 */
public class TokenVerifierPublicKeyConfigTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testLoadYamlFileFromClasspath() throws URISyntaxException {
        TokenValidatorConfig config = TokenVerifierPublicKeyConfig.readFromFileOrClasspath();
        checkConfig(config);
    }

    @Test
    public void testLoadYamlFileFromEnv() throws URISyntaxException {
        ClassLoader loader = getClass().getClassLoader();
        File configFile = new File(loader.getResource("radar-is.yml").toURI());
        environmentVariables
                .set(TokenVerifierPublicKeyConfig.LOCATION_ENV, configFile.getAbsolutePath());
        TokenValidatorConfig config = TokenVerifierPublicKeyConfig.readFromFileOrClasspath();
        checkConfig(config);
    }

    private void checkConfig(TokenValidatorConfig config) throws URISyntaxException {
        List<URI> uris = config.getPublicKeyEndpoints();
        assertThat(uris, hasItems(new URI("http://localhost:8089/oauth/token_key"),
                new URI("http://localhost:8089/oauth/token_key")));
        assertEquals(2, uris.size());
        assertEquals("unit_test", config.getResourceName());
        assertEquals(2, config.getPublicKeys().size());
        List<String> algs = config.getPublicKeys();
        assertThat(algs, hasItems(startsWith(new ECTokenValidationAlgorithm().getKeyHeader()),
                startsWith(new RSATokenValidationAlgorithm().getKeyHeader())));
    }
}
