package app.coronawarn.verification.portal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VerificationPortalHttpFilter.class)
@EnableConfigurationProperties
public class VerificationPortalHttpFilterTest {

  private static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
  private static final String INVALID_HOST = "invalid-server.local";
  private static final String INVALID_PORT = "9988";

  @Value("${route.host}")
  private String routeHost;

  @Value("${route.port}")
  private String routePort;

  @Autowired
  private VerificationPortalHttpFilter verificationPortalHttpFilter;

  @Test
  public void doFilterReturnsOkForValidHost() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    request.addHeader(HttpHeaders.HOST, routeHost);
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  public void doFilterReturnsOkForValidHostAndPort() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    request.addHeader(HttpHeaders.HOST, routeHost + ":" + routePort);
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  public void doFilterReturnsBadRequestWhenXForwardedHostHeaderInRequest() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    request.addHeader(HttpHeaders.HOST, routeHost);
    request.addHeader(X_FORWARDED_HOST_HEADER, INVALID_HOST);
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  public void doFilterReturnsBadRequestWhenHostHeaderNotInRequest() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  public void doFilterReturnsBadRequestWhenHostHeaderIsNotValid() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    request.addHeader(HttpHeaders.HOST, INVALID_HOST);
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  @Test
  public void doFilterReturnsBadRequestWhenPortInHostHeaderNotValid() throws IOException, ServletException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
    request.addHeader(HttpHeaders.HOST, routeHost + ":" + INVALID_PORT);
    verificationPortalHttpFilter.doFilter(request, response, new MockFilterChain());
    Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

}
