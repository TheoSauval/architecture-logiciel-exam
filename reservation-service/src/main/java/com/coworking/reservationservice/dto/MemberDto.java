package com.coworking.reservationservice.dto;

public class MemberDto {
    private Long id;
    private boolean suspended;
    private Integer maxConcurrentBookings;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    public Integer getMaxConcurrentBookings() { return maxConcurrentBookings; }
    public void setMaxConcurrentBookings(Integer maxConcurrentBookings) { this.maxConcurrentBookings = maxConcurrentBookings; }
}
