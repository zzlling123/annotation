package com.xinkao.erp.common.util.pwd;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码字符串种类
 * @author hys_thanks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringCounter {
	private int number; // 数字出现的次数
    private int smallLetter; // 小写字母出现的次数
    private int bigLetter; // 大写字母出现的次数
    private int symbol; // 特殊符号出现的次数

    public StringCounter initInstance(String s){
        if (StringUtils.isBlank(s)){
            return new StringCounter(0, 0, 0, 0);
        }else {
            char[] chars = s.toCharArray();
            int number = 0, smallLetter = 0, bigLetter = 0, symbol = 0;
            for (char c : chars) {
                if (c > 47 && c < 58){
                    number++;
                } else if(c > 64 && c < 91){
                    bigLetter++;
                } else if(c > 96 && c < 123){
                    smallLetter++;
                } else {
                    symbol++;
                }
            }
            return new StringCounter(number, smallLetter, bigLetter, symbol);
        }
    }

    public int getKinds(){
        int kinds = 0;
        if (this.number > 0) kinds++;
        if (this.smallLetter > 0) kinds++;
        if (this.bigLetter > 0) kinds++;
        if (this.symbol > 0) kinds++;
        return kinds;
    }
}
