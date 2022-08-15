package com.fastbean.example.entity;

import lombok.Data;

import java.math.BigDecimal;
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
public class UserDTO {
    private Integer id;
    private String name;
    private String address;
    private List<Long> ids;
    private Double price;
    private BigDecimal marketPrice;
    private Long time;
    private String type;
}
