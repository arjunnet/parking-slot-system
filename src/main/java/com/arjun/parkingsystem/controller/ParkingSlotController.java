package com.arjun.parkingsystem.controller;
import com.arjun.parkingsystem.model.BookingRecord;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arjun.parkingsystem.model.ParkingSlot;
import com.arjun.parkingsystem.service.ParkingSlotService;

@RestController
@RequestMapping("/slots")
public class ParkingSlotController {

    public record AddSlotRequest(String slotNo) {}
    public record BookRequest(String vehicleNo) {}

    private final ParkingSlotService service;

    public ParkingSlotController(ParkingSlotService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ParkingSlot> add(@RequestBody AddSlotRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addSlot(req.slotNo()));
    }

    @GetMapping("/free")
    public List<ParkingSlot> listFree() {
        return service.listFree();
    }

    @PostMapping("/{id}/book")
    public ParkingSlot book(@PathVariable Long id, @RequestBody BookRequest req) {
        return service.book(id, req.vehicleNo());
    }

    @PostMapping("/{id}/check-in")
    public ParkingSlot checkIn(@PathVariable Long id) {
        return service.checkIn(id);
    }

    @PostMapping("/{id}/exit")
    public ParkingSlot exit(@PathVariable Long id) {
        return service.exit(id);
    }
    @GetMapping("/{id}/history")
    public List<BookingRecord> history(@PathVariable Long id) {
        return service.history(id);
    }
}