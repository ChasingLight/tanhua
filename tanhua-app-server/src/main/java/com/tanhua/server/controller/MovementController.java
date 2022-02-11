package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.MovementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    /**
     * 发布动态
     * @param movement  动态基本信息：文字内容
     * @param imageContent  动态素材信息：图片动态,支持多张图片,使用数组接收
     * @return
     */
    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile[] imageContent) throws IOException {
        movementService.createMovement(movement, imageContent);
        return ResponseEntity.ok(null);
    }

    /**
     * 查看个人动态---分页
     * @param userId  用户id
     * @param page  页数
     * @param pagesize  页大小
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        // 如果get请求路径参数，没有设置用户id。则查询当前登录用户id对应的个人动态
        if (null == userId){
            userId = UserHolder.getUserId();
        }
        PageResult pr = movementService.findPageMovementByUserId(userId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查看好友动态---分页
     *   即：当前登录用户id
     * @param page  页数
     * @param pagesize  页大小
     * @return
     */
    @GetMapping
    public ResponseEntity findByUserId(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        //当前登录用户id
        Long currentUserId = UserHolder.getUserId();
        PageResult pr = movementService.findFriendMovements(currentUserId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查看推荐动态---分页[从redis中发起查询]
     */
    @GetMapping("/recommend")
    public ResponseEntity getRecommendMovements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize){

        PageResult pr = movementService.getRecommendMovements(page,pagesize);
        return ResponseEntity.ok(pr);
    }


    /**
     * 查询单条动态
     */
    @GetMapping("/{id}")
    public ResponseEntity getOneMovement(@PathVariable("id") String movementId){
        log.info("查询单条动态...");
        MovementsVo vo = movementService.getOneMovementById(movementId);
        return ResponseEntity.ok(vo);
    }

}
