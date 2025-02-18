package org.springframework.boot.ssl.pem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.util.function.ThrowingSupplier;

/**
 * {@link PemSslStore} loaded from {@link PemSslStoreDetails}.
 *
 * @author Phillip Webb
 * @see PemSslStore#load(PemSslStoreDetails)
 */
final class LoadedPemSslStore implements PemSslStore {

    private final PemSslStoreDetails details;

    private final Supplier<List<X509Certificate>> certificatesSupplier;

    private final Supplier<PrivateKey> privateKeySupplier;

    LoadedPemSslStore(PemSslStoreDetails details) {
        Assert.notNull(details, "Details must not be null");
        this.details = details;
        this.certificatesSupplier = supplier(() -> loadCertificates(details));
        this.privateKeySupplier = supplier(() -> loadPrivateKey(details));
    }

    private static <T> Supplier<T> supplier(ThrowingSupplier<T> supplier) {
        return SingletonSupplier.of(supplier.throwing(LoadedPemSslStore::asUncheckedIOException));
    }

    private static UncheckedIOException asUncheckedIOException(String message, Exception cause) {
        return new UncheckedIOException(message, (IOException) cause);
    }

    private static List<X509Certificate> loadCertificates(PemSslStoreDetails details) throws IOException {
        PemContent pemContent = PemContent.load(details.certificates());
        if (pemContent == null) {
            return null;
        }
        List<X509Certificate> certificates = pemContent.getCertificates();
        Assert.state(!CollectionUtils.isEmpty(certificates), "Loaded certificates are empty");
        return certificates;
    }

    private static PrivateKey loadPrivateKey(PemSslStoreDetails details) throws IOException {
        PemContent pemContent = PemContent.load(details.privateKey());
        return (pemContent != null) ? pemContent.getPrivateKey(details.privateKeyPassword()) : null;
    }

    @Override
    public String type() {
        return this.details.type();
    }

    @Override
    public String alias() {
        return this.details.alias();
    }

    @Override
    public String password() {
        return this.details.password();
    }

    @Override
    public List<X509Certificate> certificates() {
        return this.certificatesSupplier.get();
    }

    @Override
    public PrivateKey privateKey() {
        return this.privateKeySupplier.get();
    }

}