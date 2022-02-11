package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {
    @Autowired
    private SettingsService settingsService;

    /**
     * 通用设置查询----总-分包括【陌生人问题 & 3个通知开关(推送喜欢、评论、公告)】
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity settings(){
        SettingsVo vo = settingsService.settings();
        return ResponseEntity.ok(vo);
    }

    /**
     * 设置陌生人问题
     * 根据当前登录用户的id：如果已存在问题，直接更新；如果不存在，直接新增插入
     *
     * @param map
     * @return
     */
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map map){
        //获取参数
        String content = (String) map.get("content");  //问题内容
        settingsService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    /**
     * 通知设置
     * 根据当前登录用户的id：如果已存在通知设置，直接更新；如果不存在，直接新增插入
     *
     * @param map
     * @return
     */
    @PostMapping("notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map){
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }


    @GetMapping("/blackList")
    public ResponseEntity blackListByPage(@RequestParam(defaultValue = "1")int page,
                                          @RequestParam(defaultValue = "10")int pageSize){
        PageResult pageResult = settingsService.blacklistByPage(page, pageSize);
        return ResponseEntity.ok(pageResult);
    }


    @DeleteMapping("/blackList/{uid}")
    public ResponseEntity deleteBlackListById(@PathVariable("uid") long blackUserId){
        settingsService.deleteBlackList(blackUserId);
        return ResponseEntity.ok(null);
    }


}
