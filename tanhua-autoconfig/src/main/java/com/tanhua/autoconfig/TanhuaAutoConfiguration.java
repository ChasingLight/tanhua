package com.tanhua.autoconfig;


import com.tanhua.autoconfig.properties.AipFaceProperties;
import com.tanhua.autoconfig.properties.OssProperties;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        OssProperties.class,
        AipFaceProperties.class
})
public class TanhuaAutoConfiguration {


    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate(){
        return new AipFaceTemplate();
    }

}
