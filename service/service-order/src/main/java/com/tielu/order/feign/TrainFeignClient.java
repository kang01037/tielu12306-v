package com.tielu.order.feign;

import com.tielu.common.base.result.Result;
import com.tielu.order.dto.TrainDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-train")
public interface TrainFeignClient {

    @GetMapping("/api/train/entity/{id}")
    Result<TrainDTO> getTrainEntity(@PathVariable("id") Long id);

    @GetMapping("/api/train/number/{trainNo}")
    Result<TrainDTO> getTrainByNo(@PathVariable("trainNo") String trainNo);
}
