package com.ept.dic1.aquasafe.services;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.data.SampleDeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SampleDeviceService {

    private final SampleDeviceRepository repository;

    public SampleDeviceService(SampleDeviceRepository repository) {
        this.repository = repository;
    }

    public Optional<SampleDevice> get(Long id) {
        return repository.findById(id);
    }



    public SampleDevice get(String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber);
    }

    public SampleDevice save(SampleDevice entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SampleDevice> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SampleDevice> list(Pageable pageable, Specification<SampleDevice> filter) {
        return repository.findAll(filter, pageable);
    }


    public int count() {
        return (int) repository.count();
    }

    public List<SampleDevice> getAll() {
       return repository.findAll();
    }
}
