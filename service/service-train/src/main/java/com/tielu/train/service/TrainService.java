package com.tielu.train.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tielu.train.entity.Station;
import com.tielu.train.entity.Train;
import com.tielu.train.entity.TrainStation;

import java.util.List;

public interface TrainService extends IService<Train> {

    List<Station> getAllStations();

    Station getStationById(Long id);

    Train getTrainById(Long id);

    Train getTrainByNo(String trainNo);

    List<TrainStation> getTrainStations(Long trainId);
}
