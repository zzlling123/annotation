package com.xinkao.erp.core.captcha;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

public class UnsignedMathGenerator implements CodeGenerator {

	private static final long serialVersionUID = -5514819971774091076L;

	private static final String operators = "+-*";

	private final int numberLength;
	public UnsignedMathGenerator() {
		this(2);
	}

	public UnsignedMathGenerator(int numberLength) {
		this.numberLength = numberLength;
	}

	@Override
	public String generate() {
		final int limit = getLimit();
		int min = RandomUtil.randomInt(limit);
		int max = RandomUtil.randomInt(min, limit);
		String number1 = Integer.toString(max);
		String number2 = Integer.toString(min);
		number1 = StrUtil.padAfter(number1, this.numberLength, CharUtil.SPACE);
		number2 = StrUtil.padAfter(number2, this.numberLength, CharUtil.SPACE);

		return number1 + RandomUtil.randomChar(operators) + number2 + '=';
	}

	@Override
	public boolean verify(String code, String userInputCode) {
		int result;
		try {
			result = Integer.parseInt(userInputCode);
		} catch (NumberFormatException e) {
			return false;
		}

		final int calculateResult = (int) Calculator.conversion(code);
		return result == calculateResult;
	}

	public int getLength() {
		return this.numberLength * 2 + 2;
	}

	private int getLimit() {
		return Integer.parseInt("1" + StrUtil.repeat('0', this.numberLength));
	}
}
