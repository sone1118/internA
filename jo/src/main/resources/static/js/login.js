window.onload = function() {
//html이 다 간 후에 js발생하게끔
	function onClick(e) {
		const target = e.target.id;
		const contents = document.querySelector(".modal_contents");
		const birth_text= "<h2>메가박스</h2><p>[생일쿠폰]</p><p>- 팝콘(L) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><p>- 생일 쿠폰은 회원 정보 상 등록 되어 있는 생일 2주전, 회원 계정으로 자동 발급됩니다.</p>	<p>- VIP 회원 생일 쿠폰 '콤보 무료' 는 기존과 동일하게 지급됩니다.</p><p>- 일정 및 내용은 사정에 따라 변동 될 수 있습니다.</p><br><br><h2>휘닉스</h2><p>- 생일 할인: 리프트, 렌탈 반값 혜택(주민등록상 생일 당일, 1회만 가능)</p>	";
		const joins_text= "<h2>메가박스</h2><p>- 달 2장 영화 예매쿠폰 증정</p><p>- 팝콘(L) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 분기별 무료 예약</p><p>- 숙소 예약시 조식 2인권 쿠폰 증정</p><br><br><h2>중앙일보, JTBC</h2><p>- 이용권 50% 할인</p>";
		const level_text= "<h2>메가박스</h2><p>- 연 2회 영화 예매 쿠폰 증정</p><p>- 연 2회 팝콘(M) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 연 1회 리프트 이용권 30%할인 쿠폰 증정</p>	";
		
		if(target === "level") {
			contents.innerHTML = level_text;
		}else if(target === "joins") {
			contents.innerHTML = joins_text;
		}else if(target === "gift_logo") {
			contents.innerHTML = birth_text;
		}
		document.querySelector(".modal_wrap").style.display = "block";
		document.querySelector(".black_bg").style.display = "block";
	}
	function offClick() {
		document.querySelector(".modal_wrap").style.display = "none";
		document.querySelector(".black_bg").style.display = "none";
	}
	
	function loginClick() {
		console.log("login!!");
	}
	
	const login1 = document.querySelector("#content1");
	const login2 = document.querySelector("#content2");
	
	if(login1) {
		document.querySelector("#login_button").addEventListener("click", loginClick);
	}
	if(login2) {
		document.querySelector("#level").addEventListener("click", onClick);
		document.querySelector("#joins").addEventListener("click", onClick);
		document.querySelector("#gift_logo").addEventListener("click", onClick);
		document.querySelector(".modal_close").addEventListener("click", offClick);		
	}

};
