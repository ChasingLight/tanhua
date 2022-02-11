package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TodayBestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tanhua")
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;

    @GetMapping("/todayBest")
    public ResponseEntity todayBest(){
        TodayBest todayBest = todayBestService.todayBest();
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 根据当前登录用户---查询推荐的分页佳人列表
     * @param recommendUserDto  get请求的路径参数
     * @return
     */
    @GetMapping("/recommendation")
    public ResponseEntity todayRecommend(RecommendUserDto recommendUserDto){
        PageResult pageResult = todayBestService.todayRecommend(recommendUserDto);
        return ResponseEntity.ok(pageResult);
    }
}
