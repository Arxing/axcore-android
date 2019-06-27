package org.arxing.apiconnector;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class ApiConnectorBuilder {
    private OkHttpClient.Builder builder;

    public ApiConnectorBuilder() {
        builder = new OkHttpClient.Builder();
    }

    public ApiConnectorBuilder setSslSocketFactory(SSLSocketFactory socketFactory) {
        builder.sslSocketFactory(socketFactory);
        return this;
    }

    public ApiConnectorBuilder setSocketFactory(SocketFactory socketFactory) {
        builder.socketFactory(socketFactory);
        return this;
    }

    public ApiConnectorBuilder setRetryOnConnectionFailure(boolean retry) {
        builder.retryOnConnectionFailure(retry);
        return this;
    }

    public ApiConnectorBuilder setProxySelector(ProxySelector proxySelector) {
        builder.proxySelector(proxySelector);
        return this;
    }

    public ApiConnectorBuilder setProxyAuthenficator(Authenticator auth) {
        builder.proxyAuthenticator(auth);
        return this;
    }

    public ApiConnectorBuilder setProxy(Proxy proxy) {
        builder.proxy(proxy);
        return this;
    }

    public ApiConnectorBuilder setProtocol(List<Protocol> protocols) {
        builder.protocols(protocols);
        return this;
    }

    public ApiConnectorBuilder setHostnameVerifier(HostnameVerifier verifier) {
        builder.hostnameVerifier(verifier);
        return this;
    }

    public ApiConnectorBuilder setFollowSslRedirect(boolean redirect) {
        builder.followSslRedirects(redirect);
        return this;
    }

    public ApiConnectorBuilder setFollowRedirects(boolean redirect) {
        builder.followRedirects(redirect);
        return this;
    }

    public ApiConnectorBuilder setEventListenerFactory(EventListener.Factory factory) {
        builder.eventListenerFactory(factory);
        return this;
    }

    public ApiConnectorBuilder setEventListener(EventListener eventListener) {
        builder.eventListener(eventListener);
        return this;
    }

    public ApiConnectorBuilder setDns(Dns dns) {
        builder.dns(dns);
        return this;
    }

    public ApiConnectorBuilder setDispatcher(Dispatcher dispatcher) {
        builder.dispatcher(dispatcher);
        return this;
    }

    public ApiConnectorBuilder setConnectionSpecs(List<ConnectionSpec> connectionSpecs) {
        builder.connectionSpecs(connectionSpecs);
        return this;
    }

    public ApiConnectorBuilder setConnectionPool(ConnectionPool pool) {
        builder.connectionPool(pool);
        return this;
    }

    public ApiConnectorBuilder setCertificatePinner(CertificatePinner certificatePinner) {
        builder.certificatePinner(certificatePinner);
        return this;
    }

    public ApiConnectorBuilder setCache(Cache cache) {
        builder.cache(cache);
        return this;
    }

    public ApiConnectorBuilder addNetworkInterceptor(Interceptor interceptor) {
        builder.addNetworkInterceptor(interceptor);
        return this;
    }

    public ApiConnectorBuilder setAuthenticator(Authenticator auth) {
        builder.authenticator(auth);
        return this;
    }

    public ApiConnectorBuilder setConnectTimeout(long timeMills) {
        builder.connectTimeout(timeMills, TimeUnit.MILLISECONDS);
        return this;
    }

    public ApiConnectorBuilder setReadTimeout(long timeMills) {
        builder.readTimeout(timeMills, TimeUnit.MILLISECONDS);
        return this;
    }

    public ApiConnectorBuilder setWriteTimeout(long timeMills) {
        builder.writeTimeout(timeMills, TimeUnit.MILLISECONDS);
        return this;
    }

    public ApiConnectorBuilder setCookieJar(CookieJar cookieJar) {
        builder.cookieJar(cookieJar);
        return this;
    }

    public ApiConnector build() {
        return new ApiConnector(builder);
    }
}
