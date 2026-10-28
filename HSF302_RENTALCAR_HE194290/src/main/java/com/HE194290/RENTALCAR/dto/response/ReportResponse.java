package com.HE194290.RENTALCAR.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Dữ liệu cho dashboard/báo cáo của Admin. */
public class ReportResponse {

	private LocalDate startDate;
	private LocalDate endDate;
	private List<CarRentalResponse> rentals;
	private BigDecimal revenue = BigDecimal.ZERO;
	private long activeCount;
	private long completedCount;
	private long cancelledCount;

	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
	public List<CarRentalResponse> getRentals() { return rentals; }
	public void setRentals(List<CarRentalResponse> rentals) { this.rentals = rentals; }
	public BigDecimal getRevenue() { return revenue; }
	public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
	public long getActiveCount() { return activeCount; }
	public void setActiveCount(long activeCount) { this.activeCount = activeCount; }
	public long getCompletedCount() { return completedCount; }
	public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }
	public long getCancelledCount() { return cancelledCount; }
	public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }
	public long getTotalTransactions() { return rentals == null ? 0 : rentals.size(); }
}
