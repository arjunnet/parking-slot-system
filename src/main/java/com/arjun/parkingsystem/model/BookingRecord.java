package com.arjun.parkingsystem.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.persistence.EnumType;

@Entity
public class BookingRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long slotId;
	private String vehicleNo;
	private LocalDateTime bookedAt;
	@Enumerated(EnumType.STRING)
	private BookingStatus outcome = BookingStatus.ACTIVE;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSlotId() {
		return slotId;
	}
	public void setSlotId(Long slotId) {
		this.slotId = slotId;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public LocalDateTime getBookedAt() {
		return bookedAt;
	}
	public void setBookedAt(LocalDateTime bookedAt) {
		this.bookedAt = bookedAt;
	}
	public BookingStatus getOutcome() {
		return outcome;
	}
	public void setOutcome(BookingStatus outcome) {
		this.outcome = outcome;
	}
	

}
