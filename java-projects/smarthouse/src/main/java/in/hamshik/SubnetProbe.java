package in.hamshik;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        scheduler = Executors.newSingleThreadScheduledExecutor();
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
            registry.addOrUpdateDevice("esp-" + ip, ip, 80, "subnet:80");
        } else if (isPortReachable(ip, 8266, 150)) {
            registry.addOrUpdateDevice("esp-" + ip, ip, 8266, "subnet:8266");
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
}
