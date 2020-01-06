package com.hdl.bad_word_check;

/**
 * 违禁词过滤服务
 * 默认大小写不敏感
 * @Author HuangDL
 */
public interface BadWordFilterService {


    /**
     * 检查并替换违禁词
     * @param word
     * @return
     */
    String replaceBadWord(String word);

    /**
     * 检查字符串中是否包含违禁词
     * @param word
     * @return
     */
    boolean isBadWord(String word);

}
