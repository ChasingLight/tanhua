package com.tanhua.server;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class FaceTest {
    //设置APPID/AK/SK
    public static final String APP_ID = "25493537";
    public static final String API_KEY = "1QFR1TeN14FpipU3xzLbRuM9";
    public static final String SECRET_KEY = "UZohgtYLHCTENTYrc0nNN2Wm1RgTg91c";

    @Autowired
    private AipFaceTemplate aipFaceTemplate;
    @Test
    public void testFace(){
        String imageUrl = "https://tanhua-zrm.oss-cn-beijing.aliyuncs.com/2022/01/10/b94eba4c-783f-40bb-9188-d6eff32eb494.jpg";
        boolean rel = aipFaceTemplate.detect(imageUrl);
        System.out.println(rel);
    }


    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 调用接口
        String image = "https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg";
        String imageType = "URL";

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
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
    }

}

