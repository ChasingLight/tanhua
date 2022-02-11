package com.tanhua.server.controller;

import com.tanhua.server.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 发布评论
     */
    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map map) {
        String movementId = (String )map.get("movementId");
        String commentText = (String)map.get("commentText");

        commentsService.saveComments(movementId, commentText);

        return ResponseEntity.ok(null);
    }



}
