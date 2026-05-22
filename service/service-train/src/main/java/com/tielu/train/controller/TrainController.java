package com.tielu.train.controller;

import com.tielu.common.base.result.Result;
import com.tielu.train.entity.Station;
import com.tielu.train.entity.Train;
import com.tielu.train.entity.TrainStation;
import com.tielu.train.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("/station")
    public Result<List<Station>> getAllStations() {
        return Result.success(trainService.getAllStations());
    }

    @GetMapping("/station/{id}")
    public Result<Station> getStationById(@PathVariable Long id) {
        return Result.success(trainService.getStationById(id));
    }

    @GetMapping("/train/{id}")
    public Result<Train> getTrainById(@PathVariable Long id) {
        return Result.success(trainService.getTrainById(id));
    }

    @GetMapping("/train/entity/{id}")
    public Result<Train> getTrainEntity(@PathVariable Long id) {
        return Result.success(trainService.getTrainById(id));
    }

    @GetMapping("/train/number/{trainNo}")
    public Result<Train> getTrainByNo(@PathVariable String trainNo) {
        return Result.success(trainService.getTrainByNo(trainNo));
    }

    @GetMapping("/train-station")
    public Result<List<TrainStation>> getTrainStations(@RequestParam Long trainId) {
        return Result.success(trainService.getTrainStations(trainId));
    }
}
