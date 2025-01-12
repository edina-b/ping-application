package org.example.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UrlHelperTest {

    @Test
    void fixUrlPrefixes_incorrectUrlPrefixes_shouldFixUrls() {
        String url1 = "www.test1.com";
        String url2 = "http://test2.com";
        String url3 = "https://test3.com";
        String url4 = "test4.com";

        String fixedUrl1= UrlHelper.fixUrlPrefixes(url1);
        String fixedUrl2= UrlHelper.fixUrlPrefixes(url2);
        String fixedUrl3= UrlHelper.fixUrlPrefixes(url3);
        String fixedUrl4= UrlHelper.fixUrlPrefixes(url4);

        assertEquals("https://www.test1.com",fixedUrl1);
        assertEquals("https://www.test2.com",fixedUrl2);
        assertEquals("https://www.test3.com",fixedUrl3);
        assertEquals("https://www.test4.com",fixedUrl4);
    }
    @Test
    void fixUrlPrefixes_correctUrlPrefixes_shouldDoNothing() {
        String correctUrl = "https://www.test.com";

        String url = UrlHelper.fixUrlPrefixes(correctUrl);

        assertEquals(correctUrl,url);
    }
}