package com.arjun.parkingsystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.arjun.parkingsystem.model.BookingRecord;
import com.arjun.parkingsystem.model.BookingStatus;

public interface BookingRecordRepository extends JpaRepository<BookingRecord, Long> {
    List<BookingRecord> findBySlotIdOrderByBookedAtDesc(Long slotId);
    List<BookingRecord> findBySlotIdAndOutcome(Long slotId, BookingStatus outcome);
}