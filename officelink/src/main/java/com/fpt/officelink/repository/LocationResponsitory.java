package com.fpt.officelink.repository;

import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.entity.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationResponsitory extends CrudRepository<Location, Integer> {
        List<Location> findAllByAddressContaining(String address);

}
