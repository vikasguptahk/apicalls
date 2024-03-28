package com.example.apicalltrack;

import com.example.apicalltrack.Model.IdCounter;
import com.example.apicalltrack.Model.apicalls;
import io.netty.util.CharsetUtil;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class TransparentProxy {
    @Autowired
    private static MongoTemplate mongoTemplate;

    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(0);
        int port = 8082; // Choose the port you want to listen on
        // Create a proxy server
        DefaultHttpProxyServer.bootstrap()
                .withPort(port)
                .withTransparent(true)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFiltersAdapter filterRequest(HttpRequest originalRequest) {
                        return new HttpFiltersAdapter(originalRequest) {
                            private apicalls apicalls1;
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                // Log request details
                                System.out.println("Request URI: " + originalRequest.uri());
                                String scheme = "http";
                                HttpHeaders headers = originalRequest.headers();
                                if(headers.contains(HttpHeaderNames.HOST)){
                                    String host = headers.get(HttpHeaderNames.HOST);
                                    if(host.startsWith("https")){
                                        scheme="https";
                                    }
                                }
                                String completeuri = scheme + "://" + headers.get(HttpHeaderNames.HOST);
                                System.out.println("complete Uri: "+completeuri);
                                headers.entries().forEach(entry -> System.out.println("Request Header: " + entry.getKey() + " - " + entry.getValue()));
                                //store request in db
                                apicalls apicalls1 = new apicalls();
                                int id1=id1+ 1;
                                apicalls1.setId(id1);
                                apicalls1.setRequestUrl(originalRequest.uri());
                                apicalls1.setRequestheaders(originalRequest.headers().toString());
                                apicalls1.setTime(LocalDateTime.now());
                                apicalls1.setRequestPayloads("");
                                if (httpObject instanceof HttpContent) {
                                    HttpContent content = (HttpContent) httpObject;
                                    ByteBuf buf = content.content();
                                    String req_payloads = buf.toString(StandardCharsets.UTF_8);
                                    System.out.println("Request Payload: " + req_payloads);
                                    apicalls1.setRequestPayloads(req_payloads);
                                }

                                return null; // Pass the request through
                            }

                            @Override
                            public HttpResponse proxyToServerRequest(HttpObject httpObject) {
                                // Log response details

                                if (httpObject instanceof HttpResponse) {
                                    HttpResponse response = (HttpResponse) httpObject;
                                    HttpHeaders headers = response.headers();
                                    headers.entries().forEach(entry -> System.out.println("Response Header: " + entry.getKey() + " - " + entry.getValue()));
                                    //apicalls1.setResponseHeaders(response.headers().toString());
                                    //apicalls1.setResponseStatusCode(response.status().code());
                                }
                                //apicalls1.setResponsePayloads("");

                                if (httpObject instanceof HttpContent) {
                                    HttpContent content = (HttpContent) httpObject;
                                    ByteBuf buf = content.content();
                                    System.out.println("Response Payload: " + buf.toString(CharsetUtil.UTF_8));
                                    //apicalls1.setResponsePayloads(buf.toString(CharsetUtil.UTF_8));
                                }
                                //mongoTemplate.save(apicalls1);

                                return null; // Pass the response through
                            }
                        };
                    }
                })
                .start();

        System.out.println("Transparent proxy server started on port " + port);
    }
}
