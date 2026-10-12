package in.hamshik.DNSHandler;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import in.hamshik.WifiHandler.*;

final class MdnsDeviceDiscovery {
    private final EspDeviceRegistry registry;
    private JmDNS jmDNS;

    MdnsDeviceDiscovery(EspDeviceRegistry registry) {
        this.registry = registry;
    }

    void startDiscovery() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            jmDNS = JmDNS.create(localHost);
            jmDNS.addServiceListener("_http._tcp.", createServiceListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stopDiscovery() {
        if (jmDNS == null) return;

        JmDNS instanceToClose = jmDNS;
        jmDNS = null;

        new Thread(() -> {
            try {
                instanceToClose.close();
            } catch (IOException ignored) {
                // shutdown is best effort
            }
        }, "JmDNS-Shutdown-Thread").start();
    }

    private ServiceListener createServiceListener() {
        return new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                requestServiceDetails(event);
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                String hostName = event.getName().replace(".local", "");
                registry.removeDeviceByName(hostName);
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                ServiceInfo info = event.getInfo();
                if (info == null) return;

                String hostName = info.getName().replace(".local", "");
                if (!isEspDevice(hostName)) return;

                String ip = extractIpAddress(info);
                if (ip != null) {
                    registry.addOrUpdateDevice(hostName, ip, info.getPort(), "mDNS");
                }
            }
        };
    }

    private void requestServiceDetails(ServiceEvent event) {
        if (jmDNS != null) {
            jmDNS.requestServiceInfo(event.getType(), event.getName(), 1);
        }
    }

    private boolean isEspDevice(String name) {
        String lower = name.toLowerCase();
        return lower.contains("esp32") || lower.contains("esp8266") || lower.contains("esp");
    }

    private String extractIpAddress(ServiceInfo info) {
        String[] addresses = info.getHostAddresses();
        return (addresses != null && addresses.length > 0) ? addresses[0] : null;
    }
}
