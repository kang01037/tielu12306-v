package com.tielu.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tielu.ticket.entity.TrainSeatInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TrainSeatInventoryMapper extends BaseMapper<TrainSeatInventory> {

    @Update("UPDATE train_seat_inventory SET sold_count = sold_count + #{count}, version = version + 1 " +
            "WHERE train_id = #{trainId} AND travel_date = #{travelDate} AND seat_type = #{seatType} " +
            "AND (total_count - sold_count) >= #{count} AND version = #{version}")
    int increaseSoldCount(@Param("trainId") Long trainId,
                          @Param("travelDate") String travelDate,
                          @Param("seatType") String seatType,
                          @Param("count") Integer count,
                          @Param("version") Long version);

    @Update("UPDATE train_seat_inventory SET sold_count = sold_count - #{count}, version = version + 1 " +
            "WHERE train_id = #{trainId} AND travel_date = #{travelDate} AND seat_type = #{seatType} " +
            "AND version = #{version}")
    int decreaseSoldCount(@Param("trainId") Long trainId,
                          @Param("travelDate") String travelDate,
                          @Param("seatType") String seatType,
                          @Param("count") Integer count,
                          @Param("version") Long version);
}
