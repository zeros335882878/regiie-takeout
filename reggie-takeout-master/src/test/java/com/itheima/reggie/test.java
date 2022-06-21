package com.itheima.reggie;

import com.itheima.reggie.utils.HanZiUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Scanner;

@SpringBootTest
public class test {

    @Test
    public void test1() {

        String randomHanZi = HanZiUtil.getRandomHanZi(3);
        System.out.println(randomHanZi);
    }

}
