package com.xinkao.erp.common.util;

public class DictUtil {

	
	public static String getIdCardTypeChineseStr(Integer idCardType) {
		if(idCardType != null) {
			if(0 == idCardType) {
				return "身份证号";
			}else if(1 == idCardType) {
				return "护照";
			}else if(2 ==  idCardType) {
				 return "港澳居民来往内地通行证";
			}
		}
		return "";
	}
	
	public static String getSexChineseStr(Integer sex) {
		if(sex != null) {
			if(1 == sex) {
				return "男";
			}else if(2 == sex) {
				return "女";
			}
		}
		return "";
	}
	
	public static String getPersonTypeChineseStr(Integer personType) {
		if(personType != null) {
			if(0 == personType) {
				return "户籍人口";
			}else if(1 ==  personType) {
				return "常住人口";
			}else if(2 ==  personType) {
				return "流动人口";
			}else if(3 ==  personType) {
				return "滞留人口";
			}
		}
		return "";
	}
	
	public static String getCrowdTypeChineseStr(Integer crowdType) {
		if(crowdType != null) {
			if(1 == crowdType) {
				return "36类人群";
			}else if(2 == crowdType) {
				return "走读学生";
			}
		}
		return "";
	}
	
	public static String getHouseholdRelationChineseStr(Integer householdRelation) {
		if(householdRelation != null) {
			if(0 == householdRelation) {
				return "户主本人";
			}else if(1 == householdRelation) {
				return "配偶";
			}else if(2 == householdRelation) {
				return "子女";
			}else if(3 == householdRelation) {
				return "孙子女";
			}else if(4 == householdRelation) {
				return "父母";
			}else if(5 == householdRelation) {
				return "祖父母或外祖父母";
			}else if(6 == householdRelation) {
				return "兄弟姐妹";
			}else if(7 == householdRelation) {
				return "其他";
			}	
		}
		return "";
	}
	
	public static String getMaritalStatusChineseStr(Integer maritalStatus) {
		if(maritalStatus != null) {
			if(0 == maritalStatus) {
				return "未婚";
			}else if(1 == maritalStatus) {
				return "已婚";
			}
		}
		return "";
	}
	
	public static String getUseTypeChineseStr(Integer useType) {
		if(useType != null) {
			if(-1 == useType) {
				return "未核验";
			}else if(0 == useType){
				return "自有住房";
			}else if(1 == useType){
				return "承租房";
			}else if(2 == useType){
				return "无人居住";
			}
		}
		return "";
	}
}
