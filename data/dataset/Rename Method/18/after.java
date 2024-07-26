package org.apache.solr.client.solrj.impl;

import static org.apache.solr.common.params.CommonParams.ADMIN_PATHS;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.solr.client.solrj.ResponseParser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.IsUpdateRequest;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.apache.solr.client.solrj.util.AsyncListener;
import org.apache.solr.client.solrj.util.Cancellable;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.slf4j.MDC;

public class LBHttp2SolrClient extends LBSolrClient {
    private final Http2SolrClient solrClient;

    private LBHttp2SolrClient(Builder builder) {
        super(Arrays.asList(builder.baseSolrUrls));
        this.solrClient = builder.http2SolrClient;
        this.aliveCheckIntervalMillis = builder.aliveCheckIntervalMillis;
        this.defaultCollection = builder.defaultDataStore;
    }

    @Override
    protected SolrClient getClient(String baseUrl) {
        return solrClient;
    }

    @Override
    public ResponseParser getParser() {
        return solrClient.getParser();
    }

    @Override
    public RequestWriter getRequestWriter() {
        return solrClient.getRequestWriter();
    }

    public Set<String> getUrlParamNames() {
        return solrClient.getUrlParamNames();
    }

    public Cancellable asyncReq(Req req, AsyncListener<Rsp> asyncListener) {
        Rsp rsp = new Rsp();
        boolean isNonRetryable =
                req.request instanceof IsUpdateRequest || ADMIN_PATHS.contains(req.request.getPath());
        ServerIterator it = new ServerIterator(req, zombieServers);
        asyncListener.onStart();
        final AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicReference<Cancellable> currentCancellable = new AtomicReference<>();
        RetryListener retryListener =
                new RetryListener() {

                    @Override
                    public void onSuccess(Rsp rsp) {
                        asyncListener.onSuccess(rsp);
                    }

                    @Override
                    public void onFailure(Exception e, boolean retryReq) {
                        if (retryReq) {
                            String url;
                            try {
                                url = it.nextOrError(e);
                            } catch (SolrServerException ex) {
                                asyncListener.onFailure(e);
                                return;
                            }
                            try {
                                MDC.put("LBSolrClient.url", url);
                                synchronized (cancelled) {
                                    if (cancelled.get()) {
                                        return;
                                    }
                                    Cancellable cancellable =
                                            doRequest(url, req, rsp, isNonRetryable, it.isServingZombieServer(), this);
                                    currentCancellable.set(cancellable);
                                }
                            } finally {
                                MDC.remove("LBSolrClient.url");
                            }
                        } else {
                            asyncListener.onFailure(e);
                        }
                    }
                };
        try {
            Cancellable cancellable =
                    doRequest(
                            it.nextOrError(),
                            req,
                            rsp,
                            isNonRetryable,
                            it.isServingZombieServer(),
                            retryListener);
            currentCancellable.set(cancellable);
        } catch (SolrServerException e) {
            asyncListener.onFailure(e);
        }
        return () -> {
            synchronized (cancelled) {
                cancelled.set(true);
                if (currentCancellable.get() != null) {
                    currentCancellable.get().cancel();
                }
            }
        };
    }

    private interface RetryListener {
        void onSuccess(Rsp rsp);

        void onFailure(Exception e, boolean retryReq);
    }

    private Cancellable doRequest(
            String baseUrl,
            Req req,
            Rsp rsp,
            boolean isNonRetryable,
            boolean isZombie,
            RetryListener listener) {
        rsp.server = baseUrl;
        req.getRequest().setBasePath(baseUrl);
        return ((Http2SolrClient) getClient(baseUrl))
                .asyncRequest(
                        req.getRequest(),
                        null,
                        new AsyncListener<>() {
                            @Override
                            public void onSuccess(NamedList<Object> result) {
                                rsp.rsp = result;
                                if (isZombie) {
                                    zombieServers.remove(baseUrl);
                                }
                                listener.onSuccess(rsp);
                            }

                            @Override
                            public void onFailure(Throwable oe) {
                                try {
                                    throw (Exception) oe;
                                } catch (BaseHttpSolrClient.RemoteExecutionException e) {
                                    listener.onFailure(e, false);
                                } catch (SolrException e) {
                                    // we retry on 404 or 403 or 503 or 500
                                    // unless it's an update - then we only retry on connect exception
                                    if (!isNonRetryable && RETRY_CODES.contains(e.code())) {
                                        listener.onFailure((!isZombie) ? addZombie(baseUrl, e) : e, true);
                                    } else {
                                        // Server is alive but the request was likely malformed or invalid
                                        if (isZombie) {
                                            zombieServers.remove(baseUrl);
                                        }
                                        listener.onFailure(e, false);
                                    }
                                } catch (SocketException e) {
                                    if (!isNonRetryable || e instanceof ConnectException) {
                                        listener.onFailure((!isZombie) ? addZombie(baseUrl, e) : e, true);
                                    } else {
                                        listener.onFailure(e, false);
                                    }
                                } catch (SocketTimeoutException e) {
                                    if (!isNonRetryable) {
                                        listener.onFailure((!isZombie) ? addZombie(baseUrl, e) : e, true);
                                    } else {
                                        listener.onFailure(e, false);
                                    }
                                } catch (SolrServerException e) {
                                    Throwable rootCause = e.getRootCause();
                                    if (!isNonRetryable && rootCause instanceof IOException) {
                                        listener.onFailure((!isZombie) ? addZombie(baseUrl, e) : e, true);
                                    } else if (isNonRetryable && rootCause instanceof ConnectException) {
                                        listener.onFailure((!isZombie) ? addZombie(baseUrl, e) : e, true);
                                    } else {
                                        listener.onFailure(e, false);
                                    }
                                } catch (Exception e) {
                                    listener.onFailure(new SolrServerException(e), false);
                                }
                            }
                        });
    }

    public static class Builder {

        private final Http2SolrClient http2SolrClient;
        private final String[] baseSolrUrls;
        private long aliveCheckIntervalMillis =
                TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS); // 1 minute between checks
        protected String defaultDataStore;

        public Builder(Http2SolrClient http2Client, String... baseSolrUrls) {
            this.http2SolrClient = http2Client;
            this.baseSolrUrls = baseSolrUrls;
        }

        /**
         * LBHttpSolrServer keeps pinging the dead servers at fixed interval to find if it is alive. Use
         * this to set that interval
         *
         * @param aliveCheckInterval how often to ping for aliveness
         */
        public LBHttp2SolrClient.Builder setAliveCheckInterval(int aliveCheckInterval, TimeUnit unit) {
            if (aliveCheckInterval <= 0) {
                throw new IllegalArgumentException(
                        "Alive check interval must be " + "positive, specified value = " + aliveCheckInterval);
            }
            this.aliveCheckIntervalMillis = TimeUnit.MILLISECONDS.convert(aliveCheckInterval, unit);
            return this;
        }

        /** Sets a default data store for core- or collection-based requests. */
        public LBHttp2SolrClient.Builder withDefaultDataStore(String defaultCoreOrCollection) {
            this.defaultDataStore = defaultCoreOrCollection;
            return this;
        }

        public LBHttp2SolrClient build() {
            return new LBHttp2SolrClient(this);
        }
    }
}