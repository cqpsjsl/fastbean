package com.fastbean.example.entity;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangsonglin
 * @date 2021/12/13
 */
public enum TypeName {
    TEST_NAME("test");
    private String value;

    TypeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
