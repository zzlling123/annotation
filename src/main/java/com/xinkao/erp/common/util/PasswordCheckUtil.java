package com.xinkao.erp.common.util;

/**
 * 密码校验工具类
 * 
 * @author ：lxt
 * @date ：2021/8/23 9:54
 */
public class PasswordCheckUtil {

	/** 是否检测密码口令长度 */
	private static final boolean CHECK_PASSWORD_LENGTH = true;
	/** 密码最小长度，默认为6 */
	private static final int MIN_LENGTH = 6;
	/** 密码最大长度，默认为18 */
	private static final int MAX_LENGTH = 18;
	/** 是否包含数字 */
	private static final boolean CHECK_CONTAIN_DIGIT = true;
	/** 是否包含字母 */
	private static final boolean CHECK_CONTAIN_CASE = true;
	/** 是否区分大小写 */
	private static final boolean CHECK_DISTINGGUISH_CASE = true;
	/** 是否包含小写字母 */
	private static final boolean CHECK_LOWER_CASE = true;
	/** 是否包含大写字母 */
	private static final boolean CHECK_UPPER_CASE = true;
	/** 是否包含特殊符号 */
	private static final boolean CHECK_CONTAIN_SPECIAL_CHAR = false;
	/** 特殊符号集合 */
	private static final String SPECIAL_CHAR = "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
	/** 是否检测键盘按键横向连续 */
	private static final boolean CHECK_HORIZONTAL_KEY_SEQUENTIAL = true;
	/** 键盘物理位置横向不允许最小的连续个数 当前为开头和结尾不允许出现连续字符 */
	private static final String LIMIT_HORIZONTAL_NUM_KEY = "4";
	/** 是否检测键盘按键斜向连续 */
	private static final boolean CHECK_SLOPE_KEY_SEQUENTIAL = false;
	/** 键盘物理位置斜向不允许最小的连续个数 */
	private static final String LIMIT_SLOPE_NUM_KEY = "4";
	/** 是否检测逻辑位置连续 */
	private static final boolean CHECK_LOGIC_SEQUENTIAL = false;
	/** 密码口令中字符在逻辑位置上不允许最小的连续个数 */
	private static final String LIMIT_LOGIC_NUM_CHAR = "4";
	/** 是否检测连续字符相同 */
	private static final boolean CHECK_SEQUENTIAL_CHAR_SAME = true;
	/** 密码口令中相同字符不允许最小的连续个数 */
	private static final String LIMIT_NUM_SAME_CHAR = "4";
	/** 键盘横向方向规则 */
	private static final String[] KEYBOARD_HORIZONTAL_ARR = { "01234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm" };
	/** 键盘斜线方向规则 */
	private static final String[] KEYBOARD_SLOPE_ARR = { "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/", "=[;.", "-pl,", "0okm", "9ijn", "8uhb", "7ygv", "6tfc", "5rdx", "4esz" };
	/** 逻辑连续字符串 */
	private static final String SEQUENTIAL_CHARS = "abcdefghigklmnopqrstuvwxyz";

	/**
	 * 检测密码中字符长度
	 *
	 * @param password
	 * @return 符合长度要求 返回true
	 */
	private static boolean checkPasswordLength(String password) {
		if (password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH) {
			return true;
		}

		return false;
	}

	/**
	 * 检查密码中是否包含数字
	 *
	 * @param password
	 * @return 包含数字 返回true
	 */
	private static boolean checkContainDigit(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isDigit(pass)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查密码中是否包含字母（不区分大小写）
	 *
	 * @param password
	 * @return 包含字母 返回true
	 */
	private static boolean checkContainCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isLetter(pass)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查密码中是否包含小写字母
	 *
	 * @param password
	 * @return 包含小写字母 返回true
	 */
	private static boolean checkContainLowerCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isLowerCase(pass)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查密码中是否包含大写字母
	 *
	 * @param password
	 * @return 包含大写字母 返回true
	 */
	private static boolean checkContainUpperCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isUpperCase(pass)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查密码中是否包含特殊字符
	 *
	 * @param password
	 * @return 包含特殊字符 返回true
	 */
	private static boolean checkContainSpecialChar(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (SPECIAL_CHAR.indexOf(pass) != -1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 键盘规则匹配器 横向连续检测
	 *
	 * @param password
	 * @return 含有横向连续字符串 返回true
	 */
	private static boolean checkLateralKeyboardSite(String password) {
		String tPassword = new String(password);
		// 将字符串内所有字符转为小写
		tPassword = tPassword.toLowerCase();
		int length = tPassword.length();

		// 键盘横向规则检测
		int limitNum = Integer.parseInt(LIMIT_HORIZONTAL_NUM_KEY);
		boolean isStart = true;
		for (int i = 0; i < 2; i++) {
			String str;
			String distinguishStr;

			if (isStart) {
				str = tPassword.substring(i, i + limitNum);
				distinguishStr = password.substring(i, i + limitNum);
				isStart = false;
			} else {
				str = tPassword.substring(length - limitNum);
				distinguishStr = password.substring(length - limitNum);
			}

			for (String configStr : KEYBOARD_HORIZONTAL_ARR) {
				String revOrderStr = new StringBuffer(configStr).reverse().toString();

				// 检查包含字母(区分大小写)
				if (CHECK_DISTINGGUISH_CASE) {
					// 考虑 大写键盘匹配的情况
					String upperStr = configStr.toUpperCase();
					if ((configStr.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
						return true;
					}
					// 考虑逆序输入情况下 连续输入
					String revUpperStr = new StringBuffer(upperStr).reverse().toString();
					if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
						return true;
					}
				} else {
					if (configStr.contains(str)) {
						return true;
					}
					// 考虑逆序输入情况下 连续输入
					if (revOrderStr.contains(str)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 键盘规则匹配器 斜向规则检测
	 *
	 * @param password
	 * @return 含有斜向连续字符串 返回true
	 */
	private static boolean checkKeyboardSlantSite(String password) {
		String tPassword = new String(password);
		tPassword = tPassword.toLowerCase();
		int n = tPassword.length();

		// 键盘斜线方向规则检测
		int limitNum = Integer.parseInt(LIMIT_SLOPE_NUM_KEY);

		for (int i = 0; i + limitNum <= n; i++) {
			String str = tPassword.substring(i, i + limitNum);
			String distinguishStr = password.substring(i, i + limitNum);
			for (String configStr : KEYBOARD_SLOPE_ARR) {
				String revOrderStr = new StringBuffer(configStr).reverse().toString();
				// 检测包含字母(区分大小写)
				if (CHECK_DISTINGGUISH_CASE) {

					// 考虑 大写键盘匹配的情况
					String upperStr = configStr.toUpperCase();
					if ((configStr.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
						return true;
					}
					// 考虑逆序输入情况下 连续输入
					String revUpperStr = new StringBuffer(upperStr).reverse().toString();
					if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
						return true;
					}
				} else {
					if (configStr.contains(str)) {
						return true;
					}
					// 考虑逆序输入情况下 连续输入
					if (revOrderStr.contains(str)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 评估a-z,z-a这样的连续字符
	 *
	 * @param password
	 * @return 含有a-z,z-a连续字符串 返回true
	 */
	private static boolean checkSequentialChars(String password) {
		String tPassword = new String(password);
		// 将字符串内所有字符转为小写
		tPassword = tPassword.toLowerCase();
		int length = tPassword.length();

		// 键盘逻辑位置检测
		int limitNum = Integer.parseInt(LIMIT_LOGIC_NUM_CHAR);
		boolean isStart = true;
		for (int i = 0; i < 2; i++) {
			String str;
			String distinguishStr;

			if (isStart) {
				str = tPassword.substring(i, i + limitNum);
				distinguishStr = password.substring(i, i + limitNum);
				isStart = false;
			} else {
				str = tPassword.substring(length - limitNum);
				distinguishStr = password.substring(length - limitNum);
			}

			String revOrderStr = new StringBuffer(SEQUENTIAL_CHARS).reverse().toString();

			// 检查包含字母(区分大小写)
			if (CHECK_DISTINGGUISH_CASE) {
				// 考虑 大写键盘匹配的情况
				String upperStr = SEQUENTIAL_CHARS.toUpperCase();
				if ((SEQUENTIAL_CHARS.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
					return true;
				}
				// 考虑逆序输入情况下 连续输入
				String revUpperStr = new StringBuffer(upperStr).reverse().toString();
				if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
					return true;
				}
			} else {
				if (SEQUENTIAL_CHARS.contains(str)) {
					return true;
				}
				// 考虑逆序输入情况下 连续输入
				if (revOrderStr.contains(str)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 评估aaaa, 1111这样的相同连续字符
	 *
	 * @param password
	 * @return 含有aaaa, 1111等连续字符串 返回true
	 */
	private static boolean checkSequentialSameChars(String password) {
		String tPassword = new String(password);
		int n = tPassword.length();
		char[] pwdCharArr = tPassword.toCharArray();
		int limitNum = Integer.parseInt(LIMIT_NUM_SAME_CHAR);
		int count;
		for (int i = 0; i + limitNum <= n; i++) {
			count = 0;
			for (int j = 0; j < limitNum - 1; j++) {
				if (pwdCharArr[i + j] == pwdCharArr[i + j + 1]) {
					count++;
					if (count == limitNum - 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 评估密码中包含的字符类型是否符合要求
	 *
	 * @param password
	 * @return 符合要求 返回true
	 */
	public static boolean evalPassword(String password) {
		if (password == null || "".equals(password)) {
			return false;
		}

		// 检测长度
		if (CHECK_PASSWORD_LENGTH) {
			if (!checkPasswordLength(password)) {
				return false;
			}
		}

		// 检测包含数字
		if (CHECK_CONTAIN_DIGIT) {
			if (!checkContainDigit(password)) {
				return false;
			}
		}

		// 检测包含字母
		if (CHECK_CONTAIN_CASE) {
			if (!checkContainCase(password)) {
				return false;
			}
		}

		// 检测字母区分大小写
		if (CHECK_DISTINGGUISH_CASE) {
			// 检测包含小写字母
			if (CHECK_LOWER_CASE) {
				if (!checkContainLowerCase(password)) {
					return false;
				}
			}

			// 检测包含大写字母
			if (CHECK_UPPER_CASE) {
				if (!checkContainUpperCase(password)) {
					return false;
				}
			}
		}

		// 检测包含特殊符号
		if (CHECK_CONTAIN_SPECIAL_CHAR) {
			if (!checkContainSpecialChar(password)) {
				return false;
			}
		}

		// 检测键盘横向连续
		if (CHECK_HORIZONTAL_KEY_SEQUENTIAL) {
			if (checkLateralKeyboardSite(password)) {
				return false;
			}
		}

		// 检测键盘斜向连续
		if (CHECK_SLOPE_KEY_SEQUENTIAL) {
			if (checkKeyboardSlantSite(password)) {
				return false;
			}
		}

		// 检测逻辑位置连续
		if (CHECK_LOGIC_SEQUENTIAL) {
			if (checkSequentialChars(password)) {
				return false;
			}
		}

		// 检测相邻字符是否相同
		if (CHECK_SEQUENTIAL_CHAR_SAME) {
			if (checkSequentialSameChars(password)) {
				return false;
			}
		}

		return true;
	}

}
