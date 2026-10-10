package in.hamshik;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Discovers ESP32 / Arduino devices on the local network using:
 * 1. mDNS (_http._tcp.local) service discovery.
 * 2. Wokwi IoT Gateway / local simulator port checks.
 * 3. Subnet sweep for open HTTP ports (80, 8266).
 */
public class WiFiScanner {

    public record EspDevice(String name, String host, int port, String type) {}

    private final List<EspDevice> devices = new CopyOnWriteArrayList<>();
    private JmDNS jmDNS;
    private ScheduledExecutorService scheduler;
    private Consumer<List<EspDevice>> onDevicesFound;
    private volatile boolean scanning = false;

    // ─── 1. Public API ──────────────────────────────────────────

    /**
     * Starts background scanning for ESP32 devices.
     */
    public void startScanning(Consumer<List<EspDevice>> onDevicesFound) {
        this.onDevicesFound = onDevicesFound;
        this.devices.clear();
        this.scanning = true;

        startMdnsDiscovery();
        startSubnetDiscovery();
    }

    /**
     * Stops all active scans and shuts down network threads.
     */
    public void stopScanning() {
        this.scanning = false;
        stopScheduler();
        closeMdnsAsync();
    }

    /**
     * Returns a copy of all currently discovered devices.
     */
    public List<EspDevice> getDevices() {
        return new ArrayList<>(devices);
    }

    // ─── 2. mDNS Discovery ──────────────────────────────────────

    private void startMdnsDiscovery() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            jmDNS = JmDNS.create(localHost);
            jmDNS.addServiceListener("_http._tcp.", createServiceListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServiceListener createServiceListener() {
        return new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                requestServiceDetails(event);
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                handleServiceRemoved(event);
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                handleServiceResolved(event);
            }
        };
    }

    private void requestServiceDetails(ServiceEvent event) {
        if (jmDNS != null) {
            jmDNS.requestServiceInfo(event.getType(), event.getName(), 1);
        }
    }

    private void handleServiceResolved(ServiceEvent event) {
        ServiceInfo info = event.getInfo();
        if (info == null) return;

        String hostName = info.getName().replace(".local", "");
        if (!isEspDevice(hostName)) return;

        String ip = extractIpAddress(info);
        if (ip != null) {
            addOrUpdateDevice(hostName, ip, info.getPort(), "mDNS");
        }
    }

    private void handleServiceRemoved(ServiceEvent event) {
        String hostName = event.getName().replace(".local", "");
        removeDeviceByName(hostName);
    }

    private boolean isEspDevice(String name) {
        String lower = name.toLowerCase();
        return lower.contains("esp32") || lower.contains("esp8266") || lower.contains("esp");
    }

    private String extractIpAddress(ServiceInfo info) {
        String[] addresses = info.getHostAddresses();
        return (addresses != null && addresses.length > 0) ? addresses[0] : null;
    }

    private void closeMdnsAsync() {
        if (jmDNS == null) return;

        final JmDNS instanceToClose = jmDNS;
        jmDNS = null;

        new Thread(() -> {
            try {
                instanceToClose.close();
            } catch (IOException ignored) {}
        }, "JmDNS-Shutdown-Thread").start();
    }

    // ─── 3. Subnet & Simulator Probing ──────────────────────────

    private void startSubnetDiscovery() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::runSubnetScan, 0, 4, TimeUnit.SECONDS);
    }

    private void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private void runSubnetScan() {
        if (!scanning) return;

        probeSimulators();
        probeLocalSubnet();
    }

    private void probeSimulators() {
        // 1. Wokwi IoT Gateway forwarded port
        if (isPortReachable("127.0.0.1", 9080, 200)) {
            addOrUpdateDevice("wokwi-esp32", "127.0.0.1", 9080, "Wokwi Gateway");
        }
        // 2. Localhost standard port
        if (isPortReachable("127.0.0.1", 80, 200)) {
            addOrUpdateDevice("local-esp32", "127.0.0.1", 80, "localhost");
        }
        // 3. Wokwi direct virtual subnet
        if (isPortReachable("10.13.37.2", 80, 200)) {
            addOrUpdateDevice("wokwi-direct", "10.13.37.2", 80, "Wokwi Subnet");
        }
    }

    private void probeLocalSubnet() {
        String subnetPrefix = getSubnetPrefix();
        if (subnetPrefix == null) return;

        for (int i = 1; i <= 254; i++) {
            if (!scanning) return;
            String targetIp = subnetPrefix + "." + i;
            probeIpForEsp(targetIp);
        }
    }

    private void probeIpForEsp(String ip) {
        if (isPortReachable(ip, 80, 150)) {
            addOrUpdateDevice("esp-" + ip, ip, 80, "subnet:80");
        } else if (isPortReachable(ip, 8266, 150)) {
            addOrUpdateDevice("esp-" + ip, ip, 8266, "subnet:8266");
        }
    }

    private String getSubnetPrefix() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostAddress().replaceAll("\\.\\d+$", "");
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isPortReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ─── 4. Device State Management ─────────────────────────────

    private void addOrUpdateDevice(String name, String host, int port, String type) {
        devices.removeIf(existing -> existing.host().equals(host));
        devices.add(new EspDevice(name, host, port, type));
        notifyListener();
    }

    private void removeDeviceByName(String name) {
        devices.removeIf(device -> device.name().equalsIgnoreCase(name));
        notifyListener();
    }

    private void notifyListener() {
        if (onDevicesFound != null) {
            onDevicesFound.accept(new ArrayList<>(devices));
        }
    }
}