package com.HE194290.RENTALCAR.repository;

import com.HE194290.RENTALCAR.entity.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {

	boolean existsByCarCarId(Integer carId);

	boolean existsByCustomerCustomerId(Integer customerId);

	List<CarRental> findByCustomerCustomerIdOrderByPickupDateDesc(Integer customerId);

	List<CarRental> findAllByOrderByPickupDateDesc();

	List<CarRental> findByPickupDateBetweenOrderByPickupDateDesc(LocalDate start, LocalDate end);

	/** Id các xe đã có giao dịch (chưa hủy) chồng lên khoảng [start, end). Ngày trả trùng ngày nhận của người sau vẫn hợp lệ. */
	@Query("select distinct r.car.carId from CarRental r where r.status <> 'Cancelled' "
			+ "and r.pickupDate < :endDate and r.returnDate > :startDate")
	List<Integer> findBookedCarIds(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	/** Kiểm tra trùng lịch của một xe; excludeId dùng khi sửa giao dịch (truyền -1 nếu tạo mới). */
	@Query("select count(r) > 0 from CarRental r where r.car.carId = :carId and r.status <> 'Cancelled' "
			+ "and r.pickupDate < :endDate and r.returnDate > :startDate and r.carRentId <> :excludeId")
	boolean existsOverlap(@Param("carId") Integer carId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("excludeId") Integer excludeId);
}
