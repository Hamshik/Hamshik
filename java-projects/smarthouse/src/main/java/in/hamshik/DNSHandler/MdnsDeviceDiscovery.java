package in.hamshik.DNSHandler;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import in.hamshik.WifiHandler.*;

final class MdnsDeviceDiscovery {
    private static final System.Logger LOGGER = System.getLogger(MdnsDeviceDiscovery.class.getName());

    private final EspDeviceRegistry registry;
    private final List<JmDNS> jmDNSInstances = new CopyOnWriteArrayList<>();

    MdnsDeviceDiscovery(EspDeviceRegistry registry) {
        this.registry = registry;
    }

    void startDiscovery() {
        List<Inet4Address> addresses;
        try {
            addresses = LocalNetworkInterfaces.getIPv4Addresses();
        } catch (SocketException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to list local network interfaces", e);
            return;
        }

        for (Inet4Address address : addresses) {
            try {
                JmDNS instance = JmDNS.create(address);
                jmDNSInstances.add(instance);
                instance.addServiceListener("_http._tcp.", createServiceListener());
            } catch (IOException e) {
                LOGGER.log(System.Logger.Level.ERROR,
                        "Unable to start mDNS discovery on " + address.getHostAddress(), e);
            }
        }
        if (jmDNSInstances.isEmpty()) {
            LOGGER.log(System.Logger.Level.ERROR, "No mDNS discovery interface could be started");
        }
    }

    void stopDiscovery() {
        if (jmDNSInstances.isEmpty()) return;
        List<JmDNS> instancesToClose = List.copyOf(jmDNSInstances);
        jmDNSInstances.clear();

        Thread shutdownThread = new Thread(() -> {
            for (JmDNS instance : instancesToClose) {
                try {
                    instance.close();
                } catch (IOException e) {
                    LOGGER.log(System.Logger.Level.ERROR, "Unable to close mDNS discovery", e);
                }
            }
        }, "JmDNS-Shutdown-Thread");
        shutdownThread.setDaemon(true);
        shutdownThread.start();
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

                String ip = extractIpAddress(info);
                if (ip != null &&
                    (EspVerifier.verifyAndParseEsp(hostName, 80) 
                    || EspVerifier.verifyAndParseEsp(hostName, 8266))) {
                    registry.addOrUpdateDevice(hostName, ip, info.getPort(), "mDNS");
                }
            }
        };
    }

    private void requestServiceDetails(ServiceEvent event) {
        for (JmDNS instance : jmDNSInstances) {
            instance.requestServiceInfo(event.getType(), event.getName(), 1);
        }
    }

    private String extractIpAddress(ServiceInfo info) {
        String[] addresses = info.getHostAddresses();
        return (addresses != null && addresses.length > 0) ? addresses[0] : null;
    }
}
