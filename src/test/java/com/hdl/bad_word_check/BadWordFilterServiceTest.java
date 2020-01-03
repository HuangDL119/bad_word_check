package com.hdl.bad_word_check;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
class BadWordFilterServiceTest {
    @Autowired
    BadWordFilterService badWordFilterService;

    @Test
    void 单个违禁词检查() {
        String badWord1 = "共产党好";
        boolean result = badWordFilterService.isBadWord(badWord1);
        assertThat(result, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord1),is("*好"));
    }
    @Test
    void 多个违禁词检查() {
        String badWord1 = "共产党毛泽东东";
        boolean result = badWordFilterService.isBadWord(badWord1);
        assertThat(result, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord1),is("**东"));
    }

    @Test
    void 多字符串匹配() {
        String badWord = "中国共产党员好";
        boolean result = badWordFilterService.isBadWord(badWord);
        assertThat(result, is(true));
        //先匹配到短的
        assertThat(badWordFilterService.replaceBadWord(badWord),is("中国*员好"));
    }

    @Test
    void 有空格的字符串匹配() {
        String badWord = "我爱中国共      产  党";
        boolean result = badWordFilterService.isBadWord(badWord);
        assertThat(result, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord),is("我爱中国*"));
    }

    @Test
    void 非关键字匹配字段() {
        String badWord2 = "国民党";
        boolean result2 = badWordFilterService.isBadWord(badWord2);
        assertThat(result2, is(false));
        assertThat(badWordFilterService.replaceBadWord(badWord2),is("国民党"));
    }

    @Test
    void 程序关键字过滤() {
        String badWord2 = "delete1";
        boolean result2 = badWordFilterService.isBadWord(badWord2);
        assertThat(result2, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord2),is("*1"));
    }
    @Test
    void 大小写不敏感() {
        String badWord2 = "dEleTe1";
        boolean result2 = badWordFilterService.isBadWord(badWord2);
        assertThat(result2, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord2),is("*1"));
    }
    @Test
    void 程序关键字过滤组合() {
        String badWord2 = "11likedelete22";
        boolean result2 = badWordFilterService.isBadWord(badWord2);
        assertThat(result2, is(true));
        assertThat(badWordFilterService.replaceBadWord(badWord2),is("11**22"));
    }

}