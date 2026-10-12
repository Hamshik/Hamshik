package in.hamshik.DNSHandler;

import java.util.List;
import java.util.function.Consumer;

import in.hamshik.WifiHandler.*;;

/**
 * Discovers ESP32 / Arduino devices on the local network using:
 * 1. mDNS (_http._tcp.local) service discovery.
 * 2. Wokwi IoT Gateway / local simulator port checks.
 * 3. Subnet sweep for open HTTP ports (80, 8266).
 */

public class WiFiScanner {

    public record EspDevice(String name, String host, int port, String type) {}

    private final EspDeviceRegistry registry = new EspDeviceRegistry();
    private final MdnsDeviceDiscovery mdnsDiscovery = new MdnsDeviceDiscovery(registry);
    private final SubnetProbe subnetProbe = new SubnetProbe(registry);
    private volatile boolean scanning = false;
    private Consumer<List<EspDevice>> onDevicesFound;
    
    /**
     * Starts background scanning for ESP32 devices.
     */
    public void startScanning(Consumer<List<EspDevice>> onDevicesFound) {
        this.onDevicesFound = onDevicesFound;
        registry.setChangeListener(devices -> {
            if (scanning && this.onDevicesFound != null) {
                this.onDevicesFound.accept(List.copyOf(devices));
            }
        });
        registry.clear();
        scanning = true;

        mdnsDiscovery.startDiscovery();
        subnetProbe.startScanning();
    }

    /**
     * Stops all active scans and shuts down network threads.
     */
    public void stopScanning() {
        scanning = false;
        subnetProbe.stopScanning();
        mdnsDiscovery.stopDiscovery();
    }

    /**
     * Returns a copy of all currently discovered devices.
     */
    public List<EspDevice> getDevices() {
        return registry.snapshot();
    }
}
