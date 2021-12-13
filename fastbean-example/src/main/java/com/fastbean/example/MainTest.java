package com.fastbean.example;

import com.fastbean.example.entity.TypeName;
import com.fastbean.example.entity.UserDO;
import com.fastbean.example.entity.UserDTO;
import com.jiangsonglin.beans.FastBeanUtils;
import com.jiangsonglin.beans.LambdaNameMappingWrapper;
import com.jiangsonglin.convert.Converter;
import com.jiangsonglin.copier.BeanUtilsCopier;
import com.jiangsonglin.copier.FastBeanCopier;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/13
 */
public class MainTest {

    /**
     * 常规使用
     */
    @Test
    public void common() {
        UserDO userDO = getUserDO();
        UserDTO userDTO = new UserDTO();
        FastBeanCopier copier = FastBeanUtils.create(UserDO.class, UserDTO.class);
        copier.copy(userDO, userDTO);
        Assert.assertEquals(userDO.getId(), userDTO.getId());
    }

    /**
     * 枚举处理
     *
     * @return
     */
    @Test
    public void enumTest() {
        UserDO userDO = getUserDO();
        userDO.setType(TypeName.TEST_NAME);
        UserDTO userDTO = new UserDTO();
        FastBeanCopier copier = FastBeanUtils.create(UserDO.class, UserDTO.class);
        copier.copy(userDO, userDTO);
        System.out.println(userDTO);
        Assert.assertEquals(userDO.getType().name(), userDTO.getType());
    }

    /**
     * List使用
     *
     * @return
     */
    @Test
    public void collectionTest() {
        List<UserDO> list = new ArrayList<>();
        list.add(getUserDO());
        list.add(getUserDO());
        list.add(getUserDO());
        List<UserDTO> dtos = new ArrayList<>();
        FastBeanCopier copier = FastBeanUtils.create(UserDO.class, UserDTO.class);
        for (UserDO userDO : list) {
            UserDTO copy = copier.copy(userDO, UserDTO.class);
            dtos.add(copy);
        }
        Assert.assertEquals(list.get(0).getId(), dtos.get(0).getId());
    }

    /**
     * 泛型检查
     *
     * @return
     */
    @Test
    public void genericity() {
        //List<Integer> ids;
        UserDO userDO = getUserDO();
        // List<Long> ids;
        UserDTO userDTO = new UserDTO();
        FastBeanCopier copier = FastBeanUtils.create(UserDO.class, UserDTO.class);
        copier.copy(userDO, userDTO);
        Assert.assertEquals(userDO.getId(), userDTO.getId());
        Assert.assertNull(userDTO.getIds());
        // 类型处理
        UserDTO dto = new UserDTO();
        copier.copy(userDO, dto, (value, target) -> {
            if (value == null) return null;
            if (value instanceof List && target.equals(List.class)) {
                List<Integer> list = (List<Integer>) value;
                ArrayList<Object> list1 = new ArrayList<>();
                for (Integer i : list) {
                    list1.add(Long.valueOf(i.toString()));
                }
                return list1;
            }
            return null;
        });
        Assert.assertEquals(dto.getIds().get(0).getClass(), Long.class);
    }

    /**
     * lambda支持
     */
    @Test
    public void lambdaTest() {
        // username
        UserDO userDO = getUserDO();
        // name
        UserDTO userDTO = new UserDTO();
        LambdaNameMappingWrapper mappingWrapper = new LambdaNameMappingWrapper<UserDTO, UserDO>()
                .add(UserDTO::getName, UserDO::getUsername);
        FastBeanCopier copier = FastBeanUtils.create(UserDO.class, UserDTO.class, mappingWrapper, null);
        copier.copy(userDO, userDTO);
        Assert.assertEquals(userDO.getUsername(), userDTO.getName());
    }

    private static UserDO getUserDO() {
        UserDO userDO = new UserDO();
        userDO.setId(123);
        userDO.setUsername("fastbean");
        userDO.setAddress("fastbean.net");
        userDO.setIds(Arrays.asList(1, 2, 3, 4, 5));
        userDO.setPrice(new Double("10.01"));
        userDO.setTime(LocalDateTime.now());
        userDO.setMarketPrice(new BigDecimal("100.05"));
        return userDO;
    }
}
