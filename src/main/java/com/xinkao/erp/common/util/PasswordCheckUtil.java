package com.xinkao.erp.common.util;


public class PasswordCheckUtil {

	
	private static final boolean CHECK_PASSWORD_LENGTH = true;
	
	private static final int MIN_LENGTH = 6;
	
	private static final int MAX_LENGTH = 18;
	
	private static final boolean CHECK_CONTAIN_DIGIT = true;
	
	private static final boolean CHECK_CONTAIN_CASE = true;
	
	private static final boolean CHECK_DISTINGGUISH_CASE = true;
	
	private static final boolean CHECK_LOWER_CASE = true;
	
	private static final boolean CHECK_UPPER_CASE = true;
	
	private static final boolean CHECK_CONTAIN_SPECIAL_CHAR = false;
	
	private static final String SPECIAL_CHAR = "!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~";
	
	private static final boolean CHECK_HORIZONTAL_KEY_SEQUENTIAL = true;
	
	private static final String LIMIT_HORIZONTAL_NUM_KEY = "4";
	
	private static final boolean CHECK_SLOPE_KEY_SEQUENTIAL = false;
	
	private static final String LIMIT_SLOPE_NUM_KEY = "4";
	
	private static final boolean CHECK_LOGIC_SEQUENTIAL = false;
	
	private static final String LIMIT_LOGIC_NUM_CHAR = "4";
	
	private static final boolean CHECK_SEQUENTIAL_CHAR_SAME = true;
	
	private static final String LIMIT_NUM_SAME_CHAR = "4";
	
	private static final String[] KEYBOARD_HORIZONTAL_ARR = { "01234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm" };
	
	private static final String[] KEYBOARD_SLOPE_ARR = { "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/", "=[;.", "-pl,", "0okm", "9ijn", "8uhb", "7ygv", "6tfc", "5rdx", "4esz" };
	
	private static final String SEQUENTIAL_CHARS = "abcdefghigklmnopqrstuvwxyz";

	
	private static boolean checkPasswordLength(String password) {
		if (password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH) {
			return true;
		}

		return false;
	}

	
	private static boolean checkContainDigit(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isDigit(pass)) {
				return true;
			}
		}

		return false;
	}

	
	private static boolean checkContainCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isLetter(pass)) {
				return true;
			}
		}

		return false;
	}

	
	private static boolean checkContainLowerCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isLowerCase(pass)) {
				return true;
			}
		}

		return false;
	}

	
	private static boolean checkContainUpperCase(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (Character.isUpperCase(pass)) {
				return true;
			}
		}

		return false;
	}

	
	private static boolean checkContainSpecialChar(String password) {
		char[] chPass = password.toCharArray();

		for (char pass : chPass) {
			if (SPECIAL_CHAR.indexOf(pass) != -1) {
				return true;
			}
		}

		return false;
	}

	
	private static boolean checkLateralKeyboardSite(String password) {
		String tPassword = new String(password);

		tPassword = tPassword.toLowerCase();
		int length = tPassword.length();

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

				if (CHECK_DISTINGGUISH_CASE) {

					String upperStr = configStr.toUpperCase();
					if ((configStr.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
						return true;
					}

					String revUpperStr = new StringBuffer(upperStr).reverse().toString();
					if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
						return true;
					}
				} else {
					if (configStr.contains(str)) {
						return true;
					}

					if (revOrderStr.contains(str)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	private static boolean checkKeyboardSlantSite(String password) {
		String tPassword = new String(password);
		tPassword = tPassword.toLowerCase();
		int n = tPassword.length();

		int limitNum = Integer.parseInt(LIMIT_SLOPE_NUM_KEY);

		for (int i = 0; i + limitNum <= n; i++) {
			String str = tPassword.substring(i, i + limitNum);
			String distinguishStr = password.substring(i, i + limitNum);
			for (String configStr : KEYBOARD_SLOPE_ARR) {
				String revOrderStr = new StringBuffer(configStr).reverse().toString();

				if (CHECK_DISTINGGUISH_CASE) {

					String upperStr = configStr.toUpperCase();
					if ((configStr.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
						return true;
					}

					String revUpperStr = new StringBuffer(upperStr).reverse().toString();
					if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
						return true;
					}
				} else {
					if (configStr.contains(str)) {
						return true;
					}

					if (revOrderStr.contains(str)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	private static boolean checkSequentialChars(String password) {
		String tPassword = new String(password);

		tPassword = tPassword.toLowerCase();
		int length = tPassword.length();

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

			if (CHECK_DISTINGGUISH_CASE) {

				String upperStr = SEQUENTIAL_CHARS.toUpperCase();
				if ((SEQUENTIAL_CHARS.contains(distinguishStr)) || (upperStr.contains(distinguishStr))) {
					return true;
				}

				String revUpperStr = new StringBuffer(upperStr).reverse().toString();
				if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
					return true;
				}
			} else {
				if (SEQUENTIAL_CHARS.contains(str)) {
					return true;
				}

				if (revOrderStr.contains(str)) {
					return true;
				}
			}
		}
		return false;
	}

	
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

	
	public static boolean evalPassword(String password) {
		if (password == null || "".equals(password)) {
			return false;
		}

		if (CHECK_PASSWORD_LENGTH) {
			if (!checkPasswordLength(password)) {
				return false;
			}
		}

		if (CHECK_CONTAIN_DIGIT) {
			if (!checkContainDigit(password)) {
				return false;
			}
		}

		if (CHECK_CONTAIN_CASE) {
			if (!checkContainCase(password)) {
				return false;
			}
		}

		if (CHECK_DISTINGGUISH_CASE) {

			if (CHECK_LOWER_CASE) {
				if (!checkContainLowerCase(password)) {
					return false;
				}
			}

			if (CHECK_UPPER_CASE) {
				if (!checkContainUpperCase(password)) {
					return false;
				}
			}
		}

		if (CHECK_CONTAIN_SPECIAL_CHAR) {
			if (!checkContainSpecialChar(password)) {
				return false;
			}
		}

		if (CHECK_HORIZONTAL_KEY_SEQUENTIAL) {
			if (checkLateralKeyboardSite(password)) {
				return false;
			}
		}

		if (CHECK_SLOPE_KEY_SEQUENTIAL) {
			if (checkKeyboardSlantSite(password)) {
				return false;
			}
		}

		if (CHECK_LOGIC_SEQUENTIAL) {
			if (checkSequentialChars(password)) {
				return false;
			}
		}

		if (CHECK_SEQUENTIAL_CHAR_SAME) {
			if (checkSequentialSameChars(password)) {
				return false;
			}
		}

		return true;
	}

}
