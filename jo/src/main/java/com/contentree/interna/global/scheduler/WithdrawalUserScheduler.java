package com.contentree.interna.global.scheduler;

import java.util.Calendar;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.contentree.interna.user.entity.WithdrawalUser;
import com.contentree.interna.user.repository.WithdrawalUserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WithdrawalUserScheduler {
	
	private final WithdrawalUserRepository withdrawalUserRepository;

	public WithdrawalUserScheduler(WithdrawalUserRepository withdrawalUserRepository) {
		this.withdrawalUserRepository = withdrawalUserRepository;
	}
	
	// 매일 0시 마다 탈퇴한지 1년 된 회원이면 정보 삭제하기 
	@Scheduled(cron = "0 0 0 * * *")
    public void userPermanentRemove() {
		// 오늘 날짜 
		Calendar today = Calendar.getInstance();
		log.info("WithdrawalUserScheduler > userPermanentRemove - 호출 (탈퇴한지 1년 이상된 유저 정보 영구 삭제 스케줄러 실행), today : {}년 {}월 {}일", 
				today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DATE));
		
        List<WithdrawalUser> withdrawalUsersList = withdrawalUserRepository.findAll();
        for (WithdrawalUser withdrawalUser : withdrawalUsersList) {
        	Calendar userExpireDate = withdrawalUser.getUserExpirationDate();
        	// 유저 정보 만료 날짜가 오늘 날짜보다 이전인 경우 
        	if (userExpireDate.before(today)) {
        		withdrawalUserRepository.delete(withdrawalUser);
        		log.info("WithdrawalUserScheduler > userPermanentRemove - 탈퇴 유저 정보 영구 삭제 (userSeq : {}, userExpireDate : {}-{}-{})", 
        				withdrawalUser.getUserSeq(), userExpireDate.get(Calendar.YEAR), userExpireDate.get(Calendar.MONTH) + 1, userExpireDate.get(Calendar.DATE));
        	}
        }
        
        log.info("WithdrawalUserScheduler > userPermanentRemove - 1년 이상된 탈퇴 유저 데어터 삭제 완료");
    }
}
