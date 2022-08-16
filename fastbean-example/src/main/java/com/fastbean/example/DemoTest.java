package com.fastbean.example;

import com.fastbean.example.entity.*;
import com.jiangsonglin.fastbean.convert.DefaultConverterChain;
import com.jiangsonglin.fastbean.convert.EnumConverter;
import com.jiangsonglin.fastbean.copier.FastBeanCopier;
import com.jiangsonglin.fastbean.strategy.FastBeanStrategy;
import com.jiangsonglin.fastbean.strategy.StrategyConstant;
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
    public static void main(String[] args) {
        UserDO userDO = new UserDO();
        userDO.setId(9999);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userDO.getId());
        System.out.println(userDO.getId());
        System.out.println(userDTO.getId());
        userDTO.setId(20000);
        System.out.println(userDO.getId());
    }
    /**
     * 常规使用
     */
    @Test
    public void common() {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./proxyOutput");

        FastBeanStrategy fastBeanStrategy = new FastBeanStrategy();
        fastBeanStrategy.setCopyStrategy(StrategyConstant.DEEP_COPY);
        fastBeanStrategy.setSetNullStrategy(StrategyConstant.CAN_SET_NULL);
        FastBeanCopier.setGlobalFastBeanStrategy(fastBeanStrategy);
        UserDO userDO = getUserDO();
        UserDTO userDTO = new UserDTO();
        // 方式一
        long start = System.currentTimeMillis();
        BeanCopyUtil.chain(userDO, userDTO)
                // 允许值覆盖 单次生效
                .canCover()
                // 允许设置控制 单次生效
                .canSetNull()
                // 深克隆 单次生效
                .isDeep(true)
                .copy();
        System.out.println(System.currentTimeMillis()-start);
        // 方式二
        UserDTO copy = BeanCopyUtil.copy(userDO, UserDTO.class);
        Assert.assertEquals(userDO.getId(), userDTO.getId());
        Assert.assertEquals(userDO.getId(), copy.getId());
    }

    /**
     * 测试策略
     */
    @Test
    public void strategy() {
        UserDO userDO = getUserDO();
        UserDTO userDTO = new UserDTO();
        // do 中的id是null
        userDTO.setId(999);
        UserDTO copy = BeanCopyUtil.chain(userDO, userDTO).canSetNull().canCover().copy();
        System.out.println(userDO);
        System.out.println(copy);
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
        System.out.println(System.currentTimeMillis());
        String s = null;
        System.out.println(s == null ?"sss":"yyy");
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
        BeanCopyUtil
                // 开启链式调用
                .chain(userDO,userDTO)
                // 字段映射（可选）将DO里面username字段赋值给DTO里面的name字段
                .nameMapping(UserDTO::getName, UserDO::getUsername)
                // 字段忽略(可选) 不对DTO中的type字段赋值
                .ignore(UserDTO::getType)
                .ignore(UserDTO::getIds)
                .ignore(UserDTO::getTime)
                // 自定义转换器链（可选）
                .converterChain(new DefaultConverterChain())
                // 往转换器(自定义/默认) 添加转换器 （可选）
                .converter(new EnumConverter())
                // 复制
                .copy();
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
        Role role = new Role();
        role.setUserId(123);
        role.setRoleName("管理员");
        Permission permission = new Permission();
        permission.setPermissionId(1);
        permission.setName("权限");
        permission.setRoleId(2);
        role.setPermission(permission);
        userDO.setRole(role);
        userDO.setStrings(new String[]{"111","222"});
        userDO.setBytes("12w32".getBytes());
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(role);
        userDO.setRoles(roles);
        return userDO;
    }
}
