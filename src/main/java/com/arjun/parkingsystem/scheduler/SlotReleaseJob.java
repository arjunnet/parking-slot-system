package com.arjun.parkingsystem.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.arjun.parkingsystem.service.ParkingSlotService;

@Component
public class SlotReleaseJob {

    private final ParkingSlotService service;

    public SlotReleaseJob(ParkingSlotService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 60000) // every 1 min
    public void run() {
        service.releaseExpiredSlots();
    }
}