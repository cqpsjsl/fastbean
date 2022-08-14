package com.fastbean.example;

import com.fastbean.example.entity.TypeName;
import com.fastbean.example.entity.UserDO;
import com.fastbean.example.entity.UserDTO;
import com.jiangsonglin.fastbean.convert.DefaultConverterChain;
import com.jiangsonglin.fastbean.convert.EnumConverter;
import com.jiangsonglin.fastbean.utils.BeanCopyUtil;
import net.sf.cglib.core.DebuggingClassWriter;
import org.junit.Assert;
import org.junit.Test;

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
public class DemoTest {

    /**
     * 常规使用
     */
    @Test
    public void common() {
        UserDO userDO = getUserDO();
        UserDTO userDTO = new UserDTO();
        // 方式一
        BeanCopyUtil.copy(userDO, userDTO);
        // 方式二
        UserDTO copy = BeanCopyUtil.copy(userDO, UserDTO.class);
        Assert.assertEquals(userDO.getId(), userDTO.getId());
        Assert.assertEquals(userDO.getId(), copy.getId());
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
        // 可增加枚举处理
        BeanCopyUtil.chain(userDO,userDTO)
                .converter(new EnumConverter())
                .copy();
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
        List<UserDTO> userDTOS = BeanCopyUtil.copyList(list, UserDO.class, UserDTO.class);
        Assert.assertEquals(list.get(0).getId(), userDTOS.get(0).getId());
    }

    /**
     * 泛型检查
     *
     * @return
     */
    @Test
    public void genericity() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./proxyOutput");
        //List<Integer> ids;
        UserDO userDO = getUserDO();
        // List<Long> ids;
        UserDTO userDTO = new UserDTO();
        System.out.println(userDO);
        BeanCopyUtil.copy(userDO,userDTO);
        Assert.assertEquals(userDO.getId(), userDTO.getId());
        Assert.assertNull(userDTO.getIds());
        System.out.println(userDTO);
        // 类型处理
        UserDTO dto = new UserDTO();
        BeanCopyUtil.chain(userDO, dto)
                .converter((value, target) -> {
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
                })
                .copy();
        System.out.println(dto);
        Assert.assertEquals(dto.getIds().get(0).getClass(), Long.class);
    }

    /**
     * lambda支持
     */
    @Test
    public void lambdaTest() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./proxyOutput");
        // username
        UserDO userDO = getUserDO();
        // name
        UserDTO userDTO = new UserDTO();
        BeanCopyUtil.chain(userDO,userDTO) // 开启链式调用
                .nameMapping(UserDTO::getName, UserDO::getUsername) // 字段映射（可选）
                .nameMapping(UserDTO::getId, UserDO::getIds) // 字段映射（可选）
                .ignore(UserDTO::getAddress) // 字段忽略(可选
                .converterChain(new DefaultConverterChain()) // 自定义转换器链（可选）
                .converter(new EnumConverter()) // 往转换器(自定义/默认) 添加转换器 （可选）
                .copy(); // 复制
        BeanCopyUtil.chain(userDO,userDTO) // 开启链式调用
                .nameMapping(UserDTO::getName, UserDO::getUsername) // 字段映射（可选）
                .converterChain(new DefaultConverterChain()) // 自定义转换器链（可选）
                .converter(new EnumConverter()) // 往转换器(自定义/默认) 添加转换器 （可选）
                .copy(); // 复制
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
