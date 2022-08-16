package com.fastbean.example.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/13
 */
@Data
public class UserDO {
    private Integer id;
    private String username;
    private String address;
    private List<Integer> ids;
    private Double price;
    private BigDecimal marketPrice;
    private LocalDateTime time;
    private TypeName type;
    private String[] strings;
    private Role role;
    private List<Role> roles;
    private byte[] bytes;
}
