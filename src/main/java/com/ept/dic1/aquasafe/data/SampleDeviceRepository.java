package com.ept.dic1.aquasafe.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SampleDeviceRepository
        extends
        JpaRepository<SampleDevice, Long>,
        JpaSpecificationExecutor<SampleDevice> {

    SampleDevice findByTrackingNumber(String trackingNumber);
}
