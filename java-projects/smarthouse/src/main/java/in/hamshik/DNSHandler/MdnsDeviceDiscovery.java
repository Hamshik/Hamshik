package in.hamshik.DNSHandler;

import in.hamshik.WifiHandler.EspDeviceRegistry;

import java.util.Objects;
import java.util.function.Consumer;

final class MdnsDeviceDiscovery {
    private final EspDeviceRegistry registry;
    private final EspVerifier espVerifier;
    private final EspDiscoveryScanner scanner;

    MdnsDeviceDiscovery(EspDeviceRegistry registry, EspVerifier espVerifier,
                        Consumer<String> onDeviceRemoved) {
        this.registry = registry;
        this.espVerifier = espVerifier;
        Objects.requireNonNull(onDeviceRemoved, "onDeviceRemoved");
        this.scanner = new EspDiscoveryScanner(new EspDiscoveryScanner.DeviceFoundCallback() {
            @Override
            public void onDeviceFound(String name, String host, int port) {
                handleDeviceFound(name, host, port);
            }

            @Override
            public void onDeviceRemoved(String name) {
                registry.removeDeviceByName(name);
                onDeviceRemoved.accept(name);
            }
        });
    }

    void startDiscovery() {
        scanner.start();
    }

    void stopDiscovery() {
        scanner.stopInternal();
    }

    private void handleDeviceFound(String name, String host, int port) {
        if (!isEspDevice(host, port)) return;
        registry.addOrUpdateDevice(name, host, port, "mDNS");
    }

    private boolean isEspDevice(String host, int port) {
        return espVerifier.verifyAndParseEsp(host, port);
    }
}
