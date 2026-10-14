package in.hamshik.DNSHandler;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import in.hamshik.WifiHandler.*;

final class SubnetProbe {
    private final EspDeviceRegistry registry;
    private ScheduledExecutorService scheduler;
    private volatile boolean scanning;

    SubnetProbe(EspDeviceRegistry registry) {
        this.registry = registry;
    }

    void startScanning() {
        scanning = true;
        stopScanningInternal();
        scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "SubnetProbe");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleWithFixedDelay(this::runSubnetScan, 0, 4, TimeUnit.SECONDS);
    }

    void stopScanning() {
        scanning = false;
        stopScanningInternal();
    }

    private void stopScanningInternal() {
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
        if (isPortReachable("127.0.0.1", 9080, 200)) {
            registry.addOrUpdateDevice("wokwi-esp32", "127.0.0.1", 9080, "Wokwi Gateway");
        }
        if (isPortReachable("127.0.0.1", 80, 200)) {
            registry.addOrUpdateDevice("local-esp32", "127.0.0.1", 80, "localhost");
        }
        if (isPortReachable("10.13.37.2", 80, 200)) {
            registry.addOrUpdateDevice("wokwi-direct", "10.13.37.2", 80, "Wokwi Subnet");
        }
    }

    private void probeLocalSubnet() {
        List<Inet4Address> addresses;
        try {
            addresses = LocalNetworkInterfaces.getIPv4Addresses();
        } catch (IOException e) {
            System.getLogger(SubnetProbe.class.getName()).log(
                    System.Logger.Level.ERROR, "Unable to list local network interfaces", e);
            return;
        }

        for (Inet4Address address : addresses) {
            String[] octets = address.getHostAddress().split("\\.");
            String subnetPrefix = octets[0] + "." + octets[1] + "." + octets[2];
            for (int i = 1; i <= 254; i++) {
                if (!scanning) return;
                String targetIp = subnetPrefix + "." + i;
                if (!targetIp.equals(address.getHostAddress())) {
                    probeIpForEsp("192.168.0.102");
                }
            }
        }
    }

    private void probeIpForEsp(String ip) {
        if (isPortReachable(ip, 80, 150)) {
            registry.addOrUpdateDevice("esp-" + ip, ip, 80, "subnet:80");
        } else if (isPortReachable(ip, 8266, 150)) {
            registry.addOrUpdateDevice("esp-" + ip, ip, 8266, "subnet:8266");
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
}
