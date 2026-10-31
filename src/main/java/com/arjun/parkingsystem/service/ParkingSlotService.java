package com.arjun.parkingsystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arjun.parkingsystem.exception.SlotNotAvailableException;
import com.arjun.parkingsystem.exception.SlotNotFoundException;
import com.arjun.parkingsystem.model.BookingRecord;
import com.arjun.parkingsystem.model.BookingStatus;
import com.arjun.parkingsystem.model.ParkingSlot;
import com.arjun.parkingsystem.model.SlotStatus;
import com.arjun.parkingsystem.repository.BookingRecordRepository;
import com.arjun.parkingsystem.repository.ParkingSlotRepository;

@Service
public class ParkingSlotService {

    private static final int RESERVE_MINUTES = 10;

    private final ParkingSlotRepository repo;
    private final BookingRecordRepository historyRepo;

    public ParkingSlotService(ParkingSlotRepository repo, BookingRecordRepository historyRepo) {
        this.repo = repo;
        this.historyRepo = historyRepo;
    }

    public ParkingSlot addSlot(String slotNo) {
        if (slotNo == null || slotNo.isBlank()) {
            throw new IllegalArgumentException("slotNo is required");
        }
        if (repo.existsBySlotNo(slotNo)) {
            throw new SlotNotAvailableException("Slot already exists: " + slotNo);
        }
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNo(slotNo);
        return repo.save(slot);
    }
    public List<ParkingSlot> listFree() {
        return repo.findByStatus(SlotStatus.FREE);
    }

    @Transactional
    public ParkingSlot book(Long id, String vehicleNo) {
        ParkingSlot slot = find(id);
        releaseIfExpired(slot);
        if (slot.getStatus() != SlotStatus.FREE) {
            throw new SlotNotAvailableException("Slot not free: " + id);
        }
        LocalDateTime now = LocalDateTime.now();
        slot.setStatus(SlotStatus.RESERVED);
        slot.setVehicleNo(vehicleNo);
        slot.setReservedUntil(now.plusMinutes(RESERVE_MINUTES));

        BookingRecord rec = new BookingRecord();
        rec.setSlotId(id);
        rec.setVehicleNo(vehicleNo);
        rec.setBookedAt(now);
        historyRepo.save(rec);
        return slot;
    }

    @Transactional
    public ParkingSlot checkIn(Long id) {
        ParkingSlot slot = find(id);
        releaseIfExpired(slot);
        if (slot.getStatus() != SlotStatus.RESERVED) {
            throw new SlotNotAvailableException("Slot not reserved (expired or never booked): " + id);
        }
        slot.setStatus(SlotStatus.OCCUPIED);
        slot.setReservedUntil(null);
        updateStatus(id, BookingStatus.ACTIVE, BookingStatus.CHECKED_IN);
        return slot;
    }

    @Transactional
    public ParkingSlot exit(Long id) {
        ParkingSlot slot = find(id);
        if (slot.getStatus() != SlotStatus.OCCUPIED) {
            throw new SlotNotAvailableException("Slot not occupied: " + id);
        }
        slot.setStatus(SlotStatus.FREE);
        slot.setVehicleNo(null);
        updateStatus(id, BookingStatus.CHECKED_IN, BookingStatus.COMPLETED);
        return slot;
    }

    @Transactional
    public void releaseExpiredSlots() {
        repo.findByStatusAndReservedUntilBefore(SlotStatus.RESERVED, LocalDateTime.now())
            .forEach(this::expire);
    }

    public List<BookingRecord> history(Long id) {
        find(id);
        return historyRepo.findBySlotIdOrderByBookedAtDesc(id);
    }

    private ParkingSlot find(Long id) {
        return repo.findById(id).orElseThrow(() -> new SlotNotFoundException(id));
    }

    private void releaseIfExpired(ParkingSlot slot) {
        if (slot.getStatus() == SlotStatus.RESERVED
                && slot.getReservedUntil() != null
                && slot.getReservedUntil().isBefore(LocalDateTime.now())) {
            expire(slot);
        }
    }

    private void expire(ParkingSlot slot) {
        slot.setStatus(SlotStatus.FREE);
        slot.setVehicleNo(null);
        slot.setReservedUntil(null);
        updateStatus(slot.getId(), BookingStatus.ACTIVE, BookingStatus.EXPIRED);
        System.out.println("Auto-released slot: " + slot.getSlotNo());
    }

    private void updateStatus(Long slotId, BookingStatus from, BookingStatus to) {
        historyRepo.findBySlotIdAndOutcome(slotId, from)
                   .forEach(r -> r.setOutcome(to));
    }
}