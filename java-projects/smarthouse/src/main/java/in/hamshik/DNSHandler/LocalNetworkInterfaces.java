package in.hamshik.DNSHandler;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

final class LocalNetworkInterfaces {
    private LocalNetworkInterfaces() {}

    static List<Inet4Address> getIPv4Addresses() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (interfaces == null) return addresses;

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback()) continue;

            Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
            while (interfaceAddresses.hasMoreElements()) {
                InetAddress address = interfaceAddresses.nextElement();
                if (address instanceof Inet4Address ipv4
                        && !ipv4.isLoopbackAddress()
                        && !ipv4.isLinkLocalAddress()) {
                    addresses.add(ipv4);
                }
            }
        }
        return addresses;
    }
}
