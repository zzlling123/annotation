package com.xinkao.erp.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;


public class MoneyUtil {
	
	private static final String[] NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	
	private static final String[] IUNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰",
			"仟" };
	
	private static final String[] DUNIT = { "角", "分", "厘" };

	
	public static String toChinese(String str) {
		str = str.replaceAll(",", "");// 去掉","
		String integerStr;// 整数部分数字
		String decimalStr;// 小数部分数字

		if (str.indexOf(".") > 0) {
			integerStr = str.substring(0, str.indexOf("."));
			decimalStr = str.substring(str.indexOf(".") + 1);
		} else if (str.indexOf(".") == 0) {
			integerStr = "";
			decimalStr = str.substring(1);
		} else {
			integerStr = str;
			decimalStr = "";
		}

		if (!integerStr.equals("")) {
			integerStr = Long.toString(Long.parseLong(integerStr));
			if (integerStr.equals("0")) {
				integerStr = "";
			}
		}

		if (integerStr.length() > IUNIT.length) {
			return str;
		}

		int[] integers = toArray(integerStr);// 整数部分数字
		boolean isMust5 = isMust5(integerStr);// 设置万单位
		int[] decimals = toArray(decimalStr);// 小数部分数字
		String result = getChineseInteger(integers, isMust5);
		String resultStr = getChineseDecimal(decimals);
		if(StringUtils.isBlank(resultStr)) {
			resultStr ="整";
		}
		return result + resultStr;
	}

	
	private static int[] toArray(String number) {
		int[] array = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			array[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		return array;
	}

	
	private static String getChineseInteger(int[] integers, boolean isMust5) {
		StringBuffer chineseInteger = new StringBuffer("");
		int length = integers.length;
		for (int i = 0; i < length; i++) {


			String key = "";
			if (integers[i] == 0) {
				if ((length - i) == 13)// 万(亿)(必填)
					key = IUNIT[4];
				else if ((length - i) == 9)// 亿(必填)
					key = IUNIT[8];
				else if ((length - i) == 5 && isMust5)// 万(不必填)
					key = IUNIT[4];
				else if ((length - i) == 1)// 元(必填)
					key = IUNIT[0];

				if ((length - i) > 1 && integers[i + 1] != 0)
					key += NUMBERS[0];
			}
			chineseInteger.append(integers[i] == 0 ? key : (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
		}
		return chineseInteger.toString();
	}

	
	private static String getChineseDecimal(int[] decimals) {
		StringBuffer chineseDecimal = new StringBuffer("");
		for (int i = 0; i < decimals.length; i++) {

			if (i == 3)
				break;
			chineseDecimal.append(decimals[i] == 0 ? "" : (NUMBERS[decimals[i]] + DUNIT[i]));
		}
		return chineseDecimal.toString();
	}

	
	private static boolean isMust5(String integerStr) {
		int length = integerStr.length();
		if (length > 4) {
			String subInteger = "";
			if (length > 8) {

				subInteger = integerStr.substring(length - 8, length - 4);
			} else {
				subInteger = integerStr.substring(0, length - 4);
			}
			return Integer.parseInt(subInteger) > 0;

		} else {

			return false;

		}
	}

	
	public static String fenToYuan(String amount) {
		return fenToYuanStr(Long.valueOf(amount));
	}

	
	public static String fenToYuanStr(Long amount) {
		DecimalFormat df = new DecimalFormat("#0.00");
		BigDecimal _divisor = new BigDecimal(100);
		BigDecimal bd = new BigDecimal(amount);
		BigDecimal result = bd.divide(_divisor);
		return df.format(result);
	}

	
	public static String yuanToFen(String amount) {
		return String.valueOf(yuanStrToFen(amount));
	}

	
	public static Long yuanStrToFen(String amount) {
		BigDecimal _divisor = new BigDecimal(100);
		BigDecimal bd = new BigDecimal(amount);
		BigDecimal result = bd.multiply(_divisor);
		return result.longValue();
	}

	public static void main(String[] args) {
		String number = "100";
		System.out.println(number + " " + MoneyUtil.toChinese(number));
	}

}
