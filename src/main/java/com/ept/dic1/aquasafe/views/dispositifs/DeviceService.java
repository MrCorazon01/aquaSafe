package com.ept.dic1.aquasafe.views.dispositifs;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.data.SampleDeviceRepository;
import com.ept.dic1.aquasafe.utils.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final SampleDeviceRepository deviceRepository;

    @Autowired
    public DeviceService(SampleDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Page<SampleDevice> getAllDevices(int page, int pageSize) {

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return deviceRepository.findAll(pageRequest);
    }

    public List<Location> getAllDeviceLocations() {
        List<SampleDevice> devices = deviceRepository.findAll();

        return devices.stream()
                .map(device -> new Location(
                        device.getTrackingNumber(),
                        device.getRegion(),
                        device.getLatitude(),
                        device.getLongitude()
                ))
                .collect(Collectors.toList());
    }


}
