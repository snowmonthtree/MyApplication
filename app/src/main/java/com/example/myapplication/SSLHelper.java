package com.example.myapplication;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SSLHelper {
    public static OkHttpClient createClientWithSelfSignedCert(Context context) throws Exception {
        // 1. 加载自签名证书
        InputStream certInputStream = context.getResources().openRawResource(R.raw.myalias); // 将证书放入 res/raw 目录

        // 2. 创建 CertificateFactory 来读取证书
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(certInputStream);

        // 3. 创建 KeyStore 并将证书加载到其中
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null); // 初始化 KeyStore
        keyStore.setCertificateEntry("myalias", cert); // "myalias" 是证书别名

        // 4. 创建 TrustManagerFactory 来处理证书信任管理
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // 5. 获取系统的默认 TrustManager
        TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length == 0) {
            throw new IllegalStateException("No TrustManagers found");
        }

        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        // 6. 创建 SSLContext 并初始化
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());

        // 7. 创建 OkHttpClient 配置，使用自签名证书
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager) // 使用自定义的 SSLContext 和 TrustManager
                .hostnameVerifier((hostname, session) -> true) // 跳过主机名验证
                .build();

        return client;

    }
}
