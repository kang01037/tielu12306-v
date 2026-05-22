package com.tielu.train.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tielu.common.base.exception.BusinessException;
import com.tielu.common.base.exception.ErrorCode;
import com.tielu.train.entity.Station;
import com.tielu.train.entity.Train;
import com.tielu.train.entity.TrainStation;
import com.tielu.train.mapper.StationMapper;
import com.tielu.train.mapper.TrainMapper;
import com.tielu.train.mapper.TrainStationMapper;
import com.tielu.train.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {

    private final TrainMapper trainMapper;
    private final StationMapper stationMapper;
    private final TrainStationMapper trainStationMapper;

    @Override
    public List<Station> getAllStations() {
        LambdaQueryWrapper<Station> query = new LambdaQueryWrapper<Station>()
                .eq(Station::getStatus, 1)
                .orderByAsc(Station::getStationCode);
        return stationMapper.selectList(query);
    }

    @Override
    public Station getStationById(Long id) {
        Station station = stationMapper.selectById(id);
        if (station == null) {
            throw new BusinessException(ErrorCode.STATION_NOT_EXIST);
        }
        return station;
    }

    @Override
    public Train getTrainById(Long id) {
        Train train = trainMapper.selectById(id);
        if (train == null) {
            throw new BusinessException(ErrorCode.TRAIN_NOT_EXIST);
        }
        return train;
    }

    @Override
    public Train getTrainByNo(String trainNo) {
        LambdaQueryWrapper<Train> query = new LambdaQueryWrapper<Train>()
                .eq(Train::getTrainNo, trainNo);
        Train train = trainMapper.selectOne(query);
        if (train == null) {
            throw new BusinessException(ErrorCode.TRAIN_NOT_EXIST);
        }
        return train;
    }

    @Override
    public List<TrainStation> getTrainStations(Long trainId) {
        LambdaQueryWrapper<TrainStation> query = new LambdaQueryWrapper<TrainStation>()
                .eq(TrainStation::getTrainId, trainId)
                .orderByAsc(TrainStation::getStationSeq);
        return trainStationMapper.selectList(query);
    }
}
