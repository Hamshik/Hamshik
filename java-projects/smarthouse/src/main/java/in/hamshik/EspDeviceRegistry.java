package in.hamshik;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

final class EspDeviceRegistry {
    private final List<WiFiScanner.EspDevice> devices = new CopyOnWriteArrayList<>();
    private Consumer<List<WiFiScanner.EspDevice>> changeListener;

    void setChangeListener(Consumer<List<WiFiScanner.EspDevice>> changeListener) {
        this.changeListener = changeListener;
    }

    void clear() {
        devices.clear();
        notifyListener();
    }

    List<WiFiScanner.EspDevice> snapshot() {
        return new ArrayList<>(devices);
    }

    void addOrUpdateDevice(String name, String host, int port, String type) {
        devices.removeIf(existing -> existing.host().equals(host));
        devices.add(new WiFiScanner.EspDevice(name, host, port, type));
        notifyListener();
    }

    void removeDeviceByName(String name) {
        devices.removeIf(device -> device.name().equalsIgnoreCase(name));
        notifyListener();
    }

    private void notifyListener() {
        if (changeListener != null) {
            changeListener.accept(snapshot());
        }
    }
}
