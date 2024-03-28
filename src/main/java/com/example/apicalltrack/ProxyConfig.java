package com.example.apicalltrack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

import static org.bson.assertions.Assertions.assertNotNull;


@Component
public class ProxyConfig{
    //private static final int ProxyPort=8547;

    private HttpProxyServer proxyServer;


    //@Autowired
    public void  start(int port1) {

        // Set SSL/TLS version and cipher suite
        /*System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("https.cipherSuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        */

        System.setProperty("javax.net.ssl.debug","ssl");
        //load keystore
        System.setProperty("javax.net.ssl.keyStore","/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStore","/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts");
        String truststore = System.getProperty("javax.net.ssl.trustStore");

        System.out.println("trustore path"+truststore);
        System.setProperty("javax.net.ssl.debug","ssl");
        try{
            //SSLContext sslContext = SSLContextUtil.createInsecureSslContext();

            proxyServer = DefaultHttpProxyServer.bootstrap()
                    .withPort(port1)
                  .withManInTheMiddle(new CustomMitmManager())
                    .withAllowRequestToOriginServer(true)
                    .withAuthenticateSslClients(true)
                    .withAllowLocalOnly(false)
                   .withTransparent(true)
                    .withFiltersSource(new HttpFiltersSourceAdapter(){
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx){
                    return new HttpFiltersAdapter(originalRequest, ctx){
                        @Override
                        public HttpResponse clientToProxyRequest(HttpObject httpObject){
                            System.out.println("truststoreSL:"+System.getProperty("javax.net.ssl.trustStore"));
                            System.out.println("request: " + httpObject);
                            //i++;
                            //if(i<100) System.out.println(httpObject.toString());
                            String uri = originalRequest.uri();

                            return null;
                        }

                        @Override
                        public HttpObject serverToProxyResponse(HttpObject httpObject){
                            System.out.println("responseVk: "+httpObject);
                            if(httpObject instanceof HttpResponse){
                                HttpResponse httpResponse = (HttpResponse) httpObject;
                                int statusCode = httpResponse.status().code();
                            }
                            return httpObject;

                        }
                    };
                }
                @Override
                public int getMaximumRequestBufferSizeInBytes() {
                    return 10 * 1024 * 1024; // 10 MB
                }

                @Override
                public int getMaximumResponseBufferSizeInBytes() {
                    return 10 * 1024 * 1024; // 10 MB
                }
            })
            .start();
/*
            try{
                assertNotNull(proxyServer);
                // new request
                System.out.println("starting testing by making new request");
                URL url = new URL("https://www.example.com");
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", port1));
                URLConnection connection = url.openConnection(proxy);
                connection.connect();
            } finally {
                System.out.println("found Proxy Server: closing it ");
                if(proxyServer!=null) proxyServer.stop();
            }*/
            System.out.println("proxy on 8734");
        }catch (Exception e){
            System.out.println("error proxy starting: "+e.getMessage());
        }
    }

    public void stopProxyServer(){
        if(proxyServer!=null) proxyServer.stop();
        System.out.println("stopped proxy server");
    }
}
