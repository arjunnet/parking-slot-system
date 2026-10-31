package com.arjun.parkingsystem.repository;
import java.time.LocalDateTime;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.arjun.parkingsystem.model.ParkingSlot;
import com.arjun.parkingsystem.model.SlotStatus;


public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
	List<ParkingSlot> findByStatus(SlotStatus status);
	List<ParkingSlot> findByStatusAndReservedUntilBefore(SlotStatus status, LocalDateTime now);
	boolean existsBySlotNo(String slotNo);

}

