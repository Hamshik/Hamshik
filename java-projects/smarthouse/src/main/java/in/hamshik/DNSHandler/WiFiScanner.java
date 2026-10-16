package in.hamshik.DNSHandler;

import java.util.List;
import java.util.function.Consumer;

import in.hamshik.WifiHandler.*;

public class WiFiScanner {

    public record EspDevice(String name, String host, int port, String type, String path) {}

    private final EspDeviceRegistry registry = new EspDeviceRegistry();
    private final EspVerifier espVerifier = new EspVerifier();
    private MdnsDeviceDiscovery mdnsDiscovery;

    private volatile boolean scanning = false;
    private Consumer<List<EspDevice>> onDevicesFound;

    public void startScanning(Consumer<List<EspDevice>> onDevicesFound,
                             Consumer<String> onDeviceRemoved) {
        this.onDevicesFound = onDevicesFound;
        scanning = true;
        registry.setChangeListener(devices -> {
            if (scanning && this.onDevicesFound != null) {
                this.onDevicesFound.accept(List.copyOf(devices));
            }
        });
        registry.clear();
        mdnsDiscovery = new MdnsDeviceDiscovery(registry, espVerifier, name -> {
            if (scanning) onDeviceRemoved.accept(name);
        });

        // Start pure mDNS zero-configuration discovery
        mdnsDiscovery.startDiscovery();
    }

    public void stopScanning() {
        scanning = false;
        if (mdnsDiscovery != null) mdnsDiscovery.stopDiscovery();
    }

    public List<EspDevice> getDevices() {
        return registry.snapshot();
    }
}