package cn.jcodenest.framework.desensitize.core;

import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.desensitize.core.annotation.Address;
import cn.jcodenest.framework.desensitize.core.regex.annotation.EmailDesensitize;
import cn.jcodenest.framework.desensitize.core.regex.annotation.RegexDesensitize;
import cn.jcodenest.framework.desensitize.core.slider.annotation.*;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link DesensitizeTest} 脱敏单元测试
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@ExtendWith(MockitoExtension.class)
class DesensitizeTest {

    @Test
    void test() {
        // 准备参数
        DesensitizeDemo desensitizeDemo = new DesensitizeDemo();
        desensitizeDemo.setNickname("沉默的老李");
        desensitizeDemo.setBankCard("9988002866797031");
        desensitizeDemo.setCarLicense("粤A66666");
        desensitizeDemo.setFixedPhone("01086551122");
        desensitizeDemo.setIdCard("530321199204074611");
        desensitizeDemo.setPassword("123456");
        desensitizeDemo.setPhoneNumber("13248765917");
        desensitizeDemo.setSlider1("ABCDEFG");
        desensitizeDemo.setSlider2("ABCDEFG");
        desensitizeDemo.setSlider3("ABCDEFG");
        desensitizeDemo.setEmail("123456@email.com");
        desensitizeDemo.setRegex("你好, 我是沉默的老李");
        desensitizeDemo.setAddress("上海市闵行区古美西路10号");
        desensitizeDemo.setOrigin("沉默的老李");

        // 调用
        DesensitizeDemo d = JsonUtils.parseObject(JsonUtils.toJsonString(desensitizeDemo), DesensitizeDemo.class);

        // 断言
        assertNotNull(d);
        assertEquals("沉****", d.getNickname());
        assertEquals("998800********31", d.getBankCard());
        assertEquals("粤A6***6", d.getCarLicense());
        assertEquals("0108*****22", d.getFixedPhone());
        assertEquals("530321**********11", d.getIdCard());
        assertEquals("******", d.getPassword());
        assertEquals("132****5917", d.getPhoneNumber());
        assertEquals("#######", d.getSlider1());
        assertEquals("ABC*EFG", d.getSlider2());
        assertEquals("*******", d.getSlider3());
        assertEquals("1****@email.com", d.getEmail());
        assertEquals("你好, 我是*", d.getRegex());
        assertEquals("北京市海淀区上地十街10号*", d.getAddress());
        assertEquals("沉默的老李", d.getOrigin());
    }

    @Data
    public static class DesensitizeDemo {

        @ChineseNameDesensitize
        private String nickname;

        @BankCardDesensitize
        private String bankCard;

        @CarLicenseDesensitize
        private String carLicense;

        @FixedPhoneDesensitize
        private String fixedPhone;

        @IdCardDesensitize
        private String idCard;

        @PasswordDesensitize
        private String password;

        @MobileDesensitize
        private String phoneNumber;

        @SliderDesensitize(prefixKeep = 6, suffixKeep = 1, replacer = "#")
        private String slider1;

        @SliderDesensitize(prefixKeep = 3, suffixKeep = 3)
        private String slider2;

        @SliderDesensitize(prefixKeep = 10)
        private String slider3;

        @EmailDesensitize
        private String email;

        @RegexDesensitize(regex = "沉默的老李", replacer = "*")
        private String regex;

        @Address
        private String address;

        private String origin;
    }
}
