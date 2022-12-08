package com.contentree.interna.global.util;

import java.util.Calendar;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MaskingUtil {
	
	public String getGradeString(String grade) {
		if(grade == "GOLD") return "G";
		if(grade == "SILVER") return "S";
		return "B";	
		//"GOLD".equals(grade); true false로
	}
	
	public Boolean todayIsBirthday(Calendar birth) {
		if(birth == null) log.error("birth가 없어?");
		Calendar today = Calendar.getInstance();
		if(today.get(Calendar.MONTH) != birth.get(Calendar.MONTH)) return false;
		if(today.get(Calendar.DATE) != birth.get(Calendar.DATE)) return false;
		return true;
	} 
	
	public String getHiddenEmail(String email) {
		String[] splitedEmail = email.split("@");
		String frontString = splitedEmail[0].replaceAll("(?<=.{1}).", "*");
		String backString = splitedEmail[1].replaceAll("(?<=.{2}).", "*");
		return (frontString + "@" + backString);
	}
}