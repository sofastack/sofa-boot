package com.alipay.sofa.tracer.boot;

import com.alipay.sofa.tracer.boot.springmvc.properties.OpenTracingSpringMvcProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * @author khotyn
 */
public class ConfigurationMetaTest {
    @Test
    public void testConfigurationMetaFileExists() throws IOException, URISyntaxException {
        Enumeration<URL> springConfigurationMetadataFiles = Thread.currentThread().getContextClassLoader().getResources("META-INF/spring-configuration-metadata.json");
        boolean found = false;
        while (springConfigurationMetadataFiles.hasMoreElements()) {
            URL fileUrl = springConfigurationMetadataFiles.nextElement();
            byte[] contents = Files.readAllBytes(Paths.get(fileUrl.toURI()));
            if (new String(contents).contains(OpenTracingSpringMvcProperties.class.getName())) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Spring configuration metadata not generated", found);
    }
}