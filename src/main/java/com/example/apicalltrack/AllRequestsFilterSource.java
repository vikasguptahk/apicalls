package com.example.apicalltrack;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.springframework.stereotype.Component;

@Component
public class AllRequestsFilterSource extends HttpFiltersSourceAdapter {
    @Override
    public HttpFiltersAdapter filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        return new HttpFiltersAdapter(originalRequest) {
            @Override
            public HttpResponse clientToProxyRequest(HttpObject httpObject) {

                return null;
            }

            @Override
            public HttpObject serverToProxyResponse(HttpObject httpObject) {

                System.out.println("response: "+httpObject);
                return httpObject;
            }
        };
    }
}
