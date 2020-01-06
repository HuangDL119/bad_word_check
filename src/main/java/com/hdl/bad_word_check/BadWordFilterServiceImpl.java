package com.hdl.bad_word_check;

import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 违禁词过滤
 * 默认大小写不敏感
 * 参考文档https://www.hankcs.com/program/algorithm/implementation-and-analysis-of-aho-corasick-algorithm-in-java.html
 * 使用的开源项目地址https://github.com/hankcs/aho-corasick。
 *
 * @Author HuangDL
 */
@Slf4j
@Component
public class BadWordFilterServiceImpl implements BadWordFilterService {

    private static final String BAD_WORDS_TXT = "badwords.txt";
    public static final String REPLACE_STRING = "*";
    private final String regEx = "\\[.*?\\]";

    //固定过滤一些程序中的关键字
    private String keyWords = "'|select|update|delete|insert|truncate|char|into"
            + "|substr|declare|exec|master|drop|execute|"
            + "union|;|--|+|,|like|//|/|%|#|*|$|@|\"|http|cr|lf|<|>|(|)";

    private Trie trie;

    public BadWordFilterServiceImpl() {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(defaultResourceLoader.getResource("classpath:" + BAD_WORDS_TXT).getInputStream(),
                "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //添加程序中可能用到的固定关键字
        String[] keyWordsArray = keyWords.split("\\|");
        for (int i = 0; i < keyWordsArray.length; i++) {
            words.add(keyWordsArray[i]);
        }
        log.info("badword size is {}", words.size());
        this.trie = Trie.builder().ignoreCase().addKeywords(words).build();
        //caseInsensitive为忽略大小写
    }


    /**
     * 检查并替换违禁词
     *
     * @param word
     * @return
     */
    @Override
    public String replaceBadWord(String word) {
        word = replaceSpace(word);
        Collection<Emit> badWords = this.getBadWords(word);
        if (badWords == null || badWords.isEmpty()) {
            return word;
        }
        return replaceBadWord(badWords, word);
    }

    /**
     * 检查字符串中是否包含违禁词
     *
     * @param word
     * @return
     */
    @Override
    public boolean isBadWord(String word) {
        if (StringUtils.isEmpty(word)) {
            return false;
        }
        word = replaceSpace(word);
        Collection<Emit> emits = getBadWords(word);
        return !emits.isEmpty();
    }

    private Collection<Emit> getBadWords(String word) {
        if (StringUtils.isEmpty(word)) {
            return Collections.emptyList();
        }
        return this.trie.parseText(word);
    }

    //清空所有空格字符串
    private String replaceSpace(String word) {
        word = word.replaceAll(" ", "").trim();//半角空格
        word = word.replaceAll("　", "").trim();//全角空格
        word = word.toLowerCase();//全部转小写
        word = word.replaceAll(regEx, "");//去除所有[]包含的内容
        return word;
    }


    private String replaceBadWord(Collection<Emit> badWords, String target) {
        String regex = badWords.stream().map(w -> Pattern.quote(w.getKeyword())).collect(Collectors.joining("|"));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(target);
        return m.replaceAll(REPLACE_STRING);
    }
}
