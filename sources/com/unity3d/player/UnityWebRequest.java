package com.unity3d.player;

import com.FlorinCompany.Gom.BuildConfig;
import com.unity3d.player.b;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

class UnityWebRequest implements Runnable {
    private static final HostnameVerifier k = new HostnameVerifier() {
        public final boolean verify(String str, SSLSession sSLSession) {
            return true;
        }
    };
    private long a;
    private String b;
    private String c;
    private Map d;
    private boolean e;
    private int f;
    private long g;
    private long h;
    private boolean i;
    private boolean j;

    static {
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager());
        }
    }

    UnityWebRequest(long j2, String str, Map map, String str2, boolean z, int i2) {
        this.a = j2;
        this.b = str2;
        this.c = str;
        this.d = map;
        this.e = z;
        this.f = i2;
    }

    static void clearCookieCache(String str, String str2) {
        CookieStore cookieStore;
        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler != null && (cookieHandler instanceof CookieManager) && (cookieStore = ((CookieManager) cookieHandler).getCookieStore()) != null) {
            if (str == null) {
                cookieStore.removeAll();
                return;
            }
            try {
                URI uri = new URI((String) null, str, str2, (String) null);
                List<HttpCookie> list = cookieStore.get(uri);
                if (list != null) {
                    for (HttpCookie remove : list) {
                        cookieStore.remove(uri, remove);
                    }
                }
            } catch (URISyntaxException unused) {
                g.Log(6, String.format("UnityWebRequest: failed to parse URI %s", new Object[]{str}));
            }
        }
    }

    private static native void contentLengthCallback(long j2, int i2);

    private static native boolean downloadCallback(long j2, ByteBuffer byteBuffer, int i2);

    private static native void errorCallback(long j2, int i2, String str);

    private boolean hasTimedOut() {
        return this.f > 0 && System.currentTimeMillis() - this.g >= ((long) this.f);
    }

    private static native void headerCallback(long j2, String str, String str2);

    private static native void responseCodeCallback(long j2, int i2);

    private void runSafe() {
        StringBuilder sb;
        String str;
        AnonymousClass2 r5;
        this.g = System.currentTimeMillis();
        try {
            URL url = new URL(this.b);
            URLConnection openConnection = url.openConnection();
            openConnection.setConnectTimeout(this.f);
            openConnection.setReadTimeout(this.f);
            InputStream inputStream = null;
            if (openConnection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) openConnection;
                if (this.e) {
                    r5 = new b.C0001b() {
                        public final void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                            if (!UnityWebRequest.this.validateCertificateCallback((x509CertificateArr == null || x509CertificateArr.length <= 0) ? new byte[0] : x509CertificateArr[0].getEncoded())) {
                                throw new CertificateException();
                            }
                        }
                    };
                    httpsURLConnection.setHostnameVerifier(k);
                } else {
                    r5 = null;
                }
                SSLSocketFactory a2 = b.a((b.C0001b) r5);
                if (a2 != null) {
                    httpsURLConnection.setSSLSocketFactory(a2);
                }
            }
            if (!url.getProtocol().equalsIgnoreCase("file") || url.getHost().isEmpty()) {
                boolean z = openConnection instanceof HttpURLConnection;
                int i2 = 0;
                if (z) {
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
                        httpURLConnection.setRequestMethod(this.c);
                        httpURLConnection.setInstanceFollowRedirects(false);
                        if (this.h > 0) {
                            if (this.j) {
                                httpURLConnection.setChunkedStreamingMode(0);
                            } else {
                                httpURLConnection.setFixedLengthStreamingMode((int) this.h);
                            }
                            if (this.i) {
                                httpURLConnection.addRequestProperty("Expect", "100-continue");
                            }
                        }
                    } catch (ProtocolException e2) {
                        badProtocolCallback(e2.toString());
                        return;
                    }
                }
                Map map = this.d;
                if (map != null) {
                    for (Map.Entry entry : map.entrySet()) {
                        openConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(131072);
                if (uploadCallback((ByteBuffer) null) > 0) {
                    openConnection.setDoOutput(true);
                    try {
                        OutputStream outputStream = openConnection.getOutputStream();
                        while (true) {
                            int uploadCallback = uploadCallback(allocateDirect);
                            if (uploadCallback <= 0) {
                                break;
                            } else if (hasTimedOut()) {
                                outputStream.close();
                                errorCallback(this.a, 14, "WebRequest timed out.");
                                return;
                            } else {
                                outputStream.write(allocateDirect.array(), allocateDirect.arrayOffset(), uploadCallback);
                            }
                        }
                    } catch (Exception e3) {
                        errorCallback(e3.toString());
                        return;
                    }
                }
                if (z) {
                    try {
                        responseCodeCallback(((HttpURLConnection) openConnection).getResponseCode());
                    } catch (UnknownHostException e4) {
                        unknownHostCallback(e4.toString());
                        return;
                    } catch (SSLException e5) {
                        sslCannotConnectCallback(e5);
                        return;
                    } catch (SocketTimeoutException e6) {
                        errorCallback(this.a, 14, e6.toString());
                        return;
                    } catch (IOException e7) {
                        errorCallback(e7.toString());
                        return;
                    }
                }
                Map<String, List<String>> headerFields = openConnection.getHeaderFields();
                headerCallback(headerFields);
                if ((headerFields == null || !headerFields.containsKey("content-length")) && openConnection.getContentLength() != -1) {
                    headerCallback("content-length", String.valueOf(openConnection.getContentLength()));
                }
                if ((headerFields == null || !headerFields.containsKey("content-type")) && openConnection.getContentType() != null) {
                    headerCallback("content-type", openConnection.getContentType());
                }
                if (headerFields != null && headerFields.containsKey("Set-Cookie") && CookieHandler.getDefault() != null && (CookieHandler.getDefault() instanceof CookieManager)) {
                    CookieStore cookieStore = ((CookieManager) CookieHandler.getDefault()).getCookieStore();
                    for (String str2 : headerFields.get("Set-Cookie")) {
                        try {
                            HttpCookie httpCookie = HttpCookie.parse(str2).get(i2);
                            if (httpCookie.getPath() != null && !httpCookie.getPath().equals(BuildConfig.FLAVOR) && (httpCookie.getDomain() == null || httpCookie.getDomain().equals(url.getHost()))) {
                                URI uri = new URI(url.getProtocol(), url.getHost(), httpCookie.getPath(), (String) null);
                                httpCookie.setDomain(url.getHost());
                                cookieStore.add(uri, httpCookie);
                            }
                        } catch (IllegalArgumentException e8) {
                            sb = new StringBuilder("UnityWebRequest: error parsing cookie '");
                            sb.append(str2);
                            sb.append("': ");
                            str = e8.getMessage();
                            sb.append(str);
                            g.Log(6, sb.toString());
                            i2 = 0;
                        } catch (URISyntaxException e9) {
                            sb = new StringBuilder("UnityWebRequest: error constructing URI: ");
                            str = e9.getMessage();
                            sb.append(str);
                            g.Log(6, sb.toString());
                            i2 = 0;
                        }
                        i2 = 0;
                    }
                }
                contentLengthCallback(openConnection.getContentLength());
                try {
                    if (openConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) openConnection;
                        responseCodeCallback(httpURLConnection2.getResponseCode());
                        inputStream = httpURLConnection2.getErrorStream();
                    }
                    if (inputStream == null) {
                        inputStream = openConnection.getInputStream();
                    }
                    ReadableByteChannel newChannel = Channels.newChannel(inputStream);
                    while (true) {
                        int read = newChannel.read(allocateDirect);
                        if (read != -1) {
                            if (!hasTimedOut()) {
                                if (!downloadCallback(allocateDirect, read)) {
                                    break;
                                }
                                allocateDirect.clear();
                            } else {
                                newChannel.close();
                                errorCallback(this.a, 14, "WebRequest timed out.");
                                return;
                            }
                        } else {
                            break;
                        }
                    }
                    newChannel.close();
                } catch (UnknownHostException e10) {
                    unknownHostCallback(e10.toString());
                } catch (SSLException e11) {
                    sslCannotConnectCallback(e11);
                } catch (SocketTimeoutException e12) {
                    errorCallback(this.a, 14, e12.toString());
                } catch (IOException e13) {
                    errorCallback(this.a, 12, e13.toString());
                } catch (Exception e14) {
                    errorCallback(e14.toString());
                }
            } else {
                malformattedUrlCallback("file:// must use an absolute path");
            }
        } catch (MalformedURLException e15) {
            malformattedUrlCallback(e15.toString());
        } catch (IOException e16) {
            errorCallback(e16.toString());
        }
    }

    private static native int uploadCallback(long j2, ByteBuffer byteBuffer);

    private static native boolean validateCertificateCallback(long j2, byte[] bArr);

    /* access modifiers changed from: protected */
    public void badProtocolCallback(String str) {
        g.Log(6, String.format("UnityWebRequest: badProtocolCallback with error=%s url=%s", new Object[]{str, this.b}));
        errorCallback(this.a, 4, str);
    }

    /* access modifiers changed from: protected */
    public void contentLengthCallback(int i2) {
        contentLengthCallback(this.a, i2);
    }

    /* access modifiers changed from: protected */
    public boolean downloadCallback(ByteBuffer byteBuffer, int i2) {
        return downloadCallback(this.a, byteBuffer, i2);
    }

    /* access modifiers changed from: protected */
    public void errorCallback(String str) {
        g.Log(6, String.format("UnityWebRequest: errorCallback with error=%s url=%s", new Object[]{str, this.b}));
        errorCallback(this.a, 2, str);
    }

    /* access modifiers changed from: protected */
    public void headerCallback(String str, String str2) {
        headerCallback(this.a, str, str2);
    }

    /* access modifiers changed from: protected */
    public void headerCallback(Map map) {
        if (map != null && map.size() != 0) {
            for (Map.Entry entry : map.entrySet()) {
                String str = (String) entry.getKey();
                if (str == null) {
                    str = "Status";
                }
                for (String headerCallback : (List) entry.getValue()) {
                    headerCallback(str, headerCallback);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void malformattedUrlCallback(String str) {
        g.Log(6, String.format("UnityWebRequest: malformattedUrlCallback with error=%s url=%s", new Object[]{str, this.b}));
        errorCallback(this.a, 5, str);
    }

    /* access modifiers changed from: protected */
    public void responseCodeCallback(int i2) {
        responseCodeCallback(this.a, i2);
    }

    public void run() {
        try {
            runSafe();
        } catch (Exception e2) {
            errorCallback(e2.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public void setupTransferSettings(long j2, boolean z, boolean z2) {
        this.h = j2;
        this.i = z;
        this.j = z2;
    }

    /* access modifiers changed from: protected */
    public void sslCannotConnectCallback(SSLException sSLException) {
        int i2;
        String sSLException2 = sSLException.toString();
        g.Log(6, String.format("UnityWebRequest: sslCannotConnectCallback with error=%s url=%s", new Object[]{sSLException2, this.b}));
        Throwable th = sSLException;
        while (true) {
            if (th == null) {
                i2 = 16;
                break;
            } else if (th instanceof SSLKeyException) {
                i2 = 23;
                break;
            } else if ((th instanceof SSLPeerUnverifiedException) || (th instanceof CertPathValidatorException)) {
                i2 = 25;
            } else {
                th = th.getCause();
            }
        }
        errorCallback(this.a, i2, sSLException2);
    }

    /* access modifiers changed from: protected */
    public void unknownHostCallback(String str) {
        g.Log(6, String.format("UnityWebRequest: unknownHostCallback with error=%s url=%s", new Object[]{str, this.b}));
        errorCallback(this.a, 7, str);
    }

    /* access modifiers changed from: protected */
    public int uploadCallback(ByteBuffer byteBuffer) {
        return uploadCallback(this.a, byteBuffer);
    }

    /* access modifiers changed from: protected */
    public boolean validateCertificateCallback(byte[] bArr) {
        return validateCertificateCallback(this.a, bArr);
    }
}
