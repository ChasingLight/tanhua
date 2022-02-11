import com.tanhua.dubbo.DubboDBApplication;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DubboDBApplication.class)
public class MysqlDBTest {

    @Autowired
    private UserInfoMapper userInfoMapper;  //这里标红不影响

    @Autowired
    private UserInfoApi userInfoApi;

    // 保存方法
    @Test
    public void testSave(){
        UserInfo userInfo = userInfoMapper.selectById(1L);
        System.out.println(userInfo);
    }

    @Test
    public void testFindByIdsAndCondition(){
        List<Long> recommendUserIds = new ArrayList<>();
        recommendUserIds.add(20L);
        recommendUserIds.add(37L);
        recommendUserIds.add(52L);
        recommendUserIds.add(18L);
        recommendUserIds.add(50L);

        UserInfo userInfo = new UserInfo();
        userInfo.setAge(30);
        Map<Long, UserInfo> byIdsAndCondition = userInfoApi.findByIdsAndCondition(recommendUserIds, userInfo);

        System.out.println(byIdsAndCondition);
    }


}