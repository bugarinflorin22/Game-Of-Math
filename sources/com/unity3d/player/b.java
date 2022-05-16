package com.unity3d.player;

import android.os.Build;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class b extends SSLSocketFactory {
    private static volatile SSLSocketFactory c;
    private static volatile X509TrustManager d;
    private static final Object e = new Object[0];
    private static final Object f = new Object[0];
    private static final boolean g;
    private final SSLSocketFactory a;
    private final a b = null;

    class a implements HandshakeCompletedListener {
        public final void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
            SSLSession session = handshakeCompletedEvent.getSession();
            session.getCipherSuite();
            session.getProtocol();
            try {
                session.getPeerPrincipal().getName();
            } catch (SSLPeerUnverifiedException unused) {
            }
        }
    }

    /* renamed from: com.unity3d.player.b$b  reason: collision with other inner class name */
    public static abstract class C0001b implements X509TrustManager {
        protected X509TrustManager a = b.c();

        public final void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
            this.a.checkClientTrusted(x509CertificateArr, str);
        }

        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
            this.a.checkServerTrusted(x509CertificateArr, str);
        }

        public final X509Certificate[] getAcceptedIssuers() {
            return this.a.getAcceptedIssuers();
        }
    }

    static {
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 20) {
            z = true;
        }
        g = z;
    }

    private b(C0001b[] bVarArr) {
        SSLContext instance = SSLContext.getInstance("TLS");
        instance.init((KeyManager[]) null, bVarArr, (SecureRandom) null);
        this.a = instance.getSocketFactory();
    }

    private Socket a(Socket socket) {
        if (socket != null && (socket instanceof SSLSocket)) {
            if (g) {
                SSLSocket sSLSocket = (SSLSocket) socket;
                sSLSocket.setEnabledProtocols(sSLSocket.getSupportedProtocols());
            }
            a aVar = this.b;
            if (aVar != null) {
                ((SSLSocket) socket).addHandshakeCompletedListener(aVar);
            }
        }
        return socket;
    }

    public static SSLSocketFactory a(C0001b bVar) {
        if (bVar == null) {
            try {
                return b();
            } catch (Exception e2) {
                g.Log(5, "CustomSSLSocketFactory: Failed to create SSLSocketFactory (" + e2.getMessage() + ")");
                return null;
            }
        } else {
            return new b(new C0001b[]{bVar});
        }
    }

    private static SSLSocketFactory b() {
        synchronized (e) {
            if (c != null) {
                SSLSocketFactory sSLSocketFactory = c;
                return sSLSocketFactory;
            }
            b bVar = new b((C0001b[]) null);
            c = bVar;
            return bVar;
        }
    }

    /* access modifiers changed from: private */
    public static X509TrustManager c() {
        synchronized (f) {
            if (d != null) {
                X509TrustManager x509TrustManager = d;
                return x509TrustManager;
            }
            try {
                TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                instance.init((KeyStore) null);
                for (TrustManager trustManager : instance.getTrustManagers()) {
                    if (trustManager instanceof X509TrustManager) {
                        X509TrustManager x509TrustManager2 = (X509TrustManager) trustManager;
                        d = x509TrustManager2;
                        return x509TrustManager2;
                    }
                }
            } catch (Exception e2) {
                g.Log(5, "CustomSSLSocketFactory: Failed to find X509TrustManager (" + e2.getMessage() + ")");
            }
        }
        return null;
    }

    public final Socket createSocket() {
        return a(this.a.createSocket());
    }

    public final Socket createSocket(String str, int i) {
        return a(this.a.createSocket(str, i));
    }

    public final Socket createSocket(String str, int i, InetAddress inetAddress, int i2) {
        return a(this.a.createSocket(str, i, inetAddress, i2));
    }

    public final Socket createSocket(InetAddress inetAddress, int i) {
        return a(this.a.createSocket(inetAddress, i));
    }

    public final Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) {
        return a(this.a.createSocket(inetAddress, i, inetAddress2, i2));
    }

    public final Socket createSocket(Socket socket, String str, int i, boolean z) {
        return a(this.a.createSocket(socket, str, i, z));
    }

    public final String[] getDefaultCipherSuites() {
        return this.a.getDefaultCipherSuites();
    }

    public final String[] getSupportedCipherSuites() {
        return this.a.getSupportedCipherSuites();
    }
}
