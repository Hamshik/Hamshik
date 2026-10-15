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

public class EspDiscoveryScanner {
    private static final System.Logger LOGGER = System.getLogger(EspDiscoveryScanner.class.getName());
    private static final String HTTP_SERVICE_TYPE = "_http._tcp.local.";

    @FunctionalInterface
    public interface DeviceFoundCallback {
        void onDeviceFound(String name, String host, int port);

        default void onDeviceRemoved(String name) {}
    }

    private final List<JmDNS> jmdnsInstances = new CopyOnWriteArrayList<>();
    private final DeviceFoundCallback callback;
    private volatile boolean running;
    private Thread thread;

    public EspDiscoveryScanner(DeviceFoundCallback callback) {
        this.callback = callback;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this::scanAvailableInterfaces, "Wifi Scanner");
        thread.setDaemon(true);
        thread.start();
    }

    private void scanAvailableInterfaces() {
        List<Inet4Address> addresses;
        try {
            addresses = LocalNetworkInterfaces.getIPv4Addresses();
        } catch (SocketException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to list local network interfaces", e);
            running = false;
            return;
        }

        for (Inet4Address address : addresses) {
            if (!running) return;
            startDiscoveryOnInterface(address);
        }

        if (jmdnsInstances.isEmpty()) {
            LOGGER.log(System.Logger.Level.ERROR, "No mDNS discovery interface could be started");
        }
    }

    private void startDiscoveryOnInterface(Inet4Address address) {
        JmDNS instance = null;
        try {
            instance = JmDNS.create(address);
            instance.addServiceListener(HTTP_SERVICE_TYPE, createServiceListener(instance));
            synchronized (this) {
                if (running) {
                    jmdnsInstances.add(instance);
                    return;
                }
            }
            closeInstance(instance);
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR,
                    "Unable to start mDNS discovery on " + address.getHostAddress(), e);
            closeInstance(instance);
        }
    }

    private ServiceListener createServiceListener(JmDNS instance) {
        return new ServiceListener() {
            @Override
            public void serviceAdded(ServiceEvent event) {
                instance.requestServiceInfo(event.getType(), event.getName(), 1);
            }

            @Override
            public void serviceRemoved(ServiceEvent event) {
                callback.onDeviceRemoved(event.getName().replace(".local", ""));
            }

            @Override
            public void serviceResolved(ServiceEvent event) {
                ServiceInfo info = event.getInfo();
                if (info == null) return;

                String[] addresses = info.getHostAddresses();
                if (addresses == null || addresses.length == 0) return;

                String name = info.getName().replace(".local", "");
                callback.onDeviceFound(name, addresses[0], info.getPort());
            }
        };
    }

    public void stopInternal() {
        List<JmDNS> instancesToClose;
        synchronized (this) {
            if (!running && jmdnsInstances.isEmpty()) return;
            running = false;
            if (thread != null) thread.interrupt();
            instancesToClose = List.copyOf(jmdnsInstances);
            jmdnsInstances.clear();
        }

        Thread shutdownThread = new Thread(() -> closeInstances(instancesToClose), "JmDNS-Shutdown-Thread");
        shutdownThread.setDaemon(true);
        shutdownThread.start();
    }

    private void closeInstances(List<JmDNS> instances) {
        for (JmDNS instance : instances) {
            closeInstance(instance);
        }
    }

    private void closeInstance(JmDNS instance) {
        if (instance == null) return;
        try {
            instance.close();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to close mDNS discovery", e);
        }
    }
} 
