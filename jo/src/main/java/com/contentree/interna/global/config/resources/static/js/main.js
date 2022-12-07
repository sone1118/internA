const offClick = () => {
	document.querySelector(".modal_wrap").style.display = "none";
	document.querySelector(".black_bg").style.display = "none";
};

const onGradeClick = (e) => {
	const target = e.target;
	const gold_level = "<h2>GOLD</h2><br><h2>메가박스</h2><p>- 연 2회 영화 예매 쿠폰 증정</p><p>- 연 2회 팝콘(M) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 연 1회 리프트 이용권 30%할인 쿠폰 증정</p>	";
	const silver_level = "<h2>SILVER</h2><br><h2>메가박스</h2><p>- 연 2회 영화 예매 쿠폰 증정</p><p>- 연 2회 팝콘(M) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 연 1회 리프트 이용권 30%할인 쿠폰 증정</p>	";
	const bronze_level = "<h2>BRONZE</h2><br><h2>메가박스</h2><p>- 연 2회 영화 예매 쿠폰 증정</p><p>- 연 2회 팝콘(M) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 연 1회 리프트 이용권 30%할인 쿠폰 증정</p>	";

	if(target.innerText === "G") setModalContents(gold_level);
	else if(target.innerText === "S") setModalContents(silver_level);
	else if(target.innerText === "B") setModalContents(bronze_level);
	
};

const onRoleClick = () => {
	const role_text= "<h2>메가박스</h2><p>- 달 2장 영화 예매쿠폰 증정</p><p>- 팝콘(L) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><br><br><h2>휘닉스</h2><p>- 분기별 무료 예약</p><p>- 숙소 예약시 조식 2인권 쿠폰 증정</p><br><br><h2>중앙일보, JTBC</h2><p>- 이용권 50% 할인</p>";
	setModalContents(role_text);
};

const onGiftLogoClick = () => {
	const birth_text= "<h2>메가박스</h2><p>[생일쿠폰]</p><p>- 팝콘(L) 무료: 오리지날 또는 카라멜 중 택1(맛 변경, 업그레이드 등 불가)</p><p>- 생일 쿠폰은 회원 정보 상 등록 되어 있는 생일 2주전, 회원 계정으로 자동 발급됩니다.</p>	<p>- VIP 회원 생일 쿠폰 '콤보 무료' 는 기존과 동일하게 지급됩니다.</p><p>- 일정 및 내용은 사정에 따라 변동 될 수 있습니다.</p><br><br><h2>휘닉스</h2><p>- 생일 할인: 리프트, 렌탈 반값 혜택(주민등록상 생일 당일, 1회만 가능)</p>	";
	setModalContents(birth_text);
};

const setModalContents = (content) => {
	const contents = document.querySelector(".modal_contents");
	contents.innerHTML = content;
	document.querySelector(".modal_wrap").style.display = "block";
	document.querySelector(".black_bg").style.display = "block";
};

const home = () => {
	const grade = document.querySelector("#grade");
	const role = document.querySelector("#role");
	const gift_logo = document.querySelector("#gift_logo");
	const modal_close = document.querySelector(".modal_close");
	
	modal_close.addEventListener("click", offClick);
	
	grade.addEventListener("click", onGradeClick);
	
	if(role) role.addEventListener("click", onRoleClick);
	
	if(gift_logo) gift_logo.addEventListener("click", onGiftLogoClick);	
};

home();