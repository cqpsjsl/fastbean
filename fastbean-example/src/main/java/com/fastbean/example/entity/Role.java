package com.fastbean.example.entity;

import lombok.Data;

/**
 * @author kedron
 * @version 1.0
 * @©copyright qq1066666261
 */
@Data
public class Role{
    private Integer userId;
    private String roleName;
    private Permission permission;
}
