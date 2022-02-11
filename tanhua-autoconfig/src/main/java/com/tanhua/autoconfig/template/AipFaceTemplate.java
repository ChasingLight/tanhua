package com.tanhua.autoconfig.template;


import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipFaceTemplate {

    @Autowired
    private AipFace client;

    /**
     * 检测图片中是否包含人脸
     *  true：包含
     *  false：不包含
     */
    public boolean detect(String imageUrl) {
        // 调用接口
        String imageType = "URL";

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测
        JSONObject res = client.detect(imageUrl, imageType, options);
        System.out.println(res.toString(2));

        /* 执行结果
        {
            "result": null,
                "log_id": 7510175895525,
                "error_msg": "image download fail",
                "cached": 0,
                "error_code": 222204,
                "timestamp": 1641815136
        }*/

        Integer error_code = (Integer) res.get("error_code");

        return error_code == 0;
    }
}
