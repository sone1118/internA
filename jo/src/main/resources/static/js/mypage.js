function onDelete() {
	deleteCookie("refresh");
	deleteCookie("access");
	location.href = "http://localhost:8080/jo/";
}

//리플레쉬가 있으면 로그인된 user
if(getCookie("refresh")) {
	const header = document.querySelector(".header");
	const userName = document.querySelector("#userName");
	const userEmail = document.querySelector("#userEmail");
	const userBirth = document.querySelector("#userBirth");
	const userCreateAt = document.querySelector("#userCreateAt");
	const userGrade = document.querySelector("#userGrade");
	const logout = document.querySelector("#logout");
	
	if(header !== null) header.classList.remove("hidden");

	//access 토큰이 없다 새 토큰 요청
	if(getCookie("access") === null) {
		//header에 refresh 넣어서
		//fetch 해서 access 토큰 받아옴
		setCookie('access', 'aaaaaa212144444', 1);
	}
	//access 값이 있을때
	//access를 보내서 받아오고 나서 user값 받아오기 fetch
	//임시 객체
	const user = {
	userSeq: 1,
	userName: "김지슬",
	userEmail: "sone1118@naver.com",
	userBirth: "19980122",
	userRole: "ADMIN",
	userGrade: "BRONZE",
	userAgreeMarketing: true,
	userAgreeSns: true};
	
	//화면에 값을 넣어 뿌림
	userName.innerText = user.userName;
	let [id, email] = user.userEmail.split("@");
	let regex = /[A-Za-z0-9]/g;
	id = id.replace(regex, "*");
	userEmail.innerText = id + "@" + email;
	userBirth.innerText = "****.**.**";
	userCreateAt.innerText = user.userBirth;
	userGrade.innerText = user.userGrade;
	logout.addEventListener("click", onDelete);

} else {
	location.href = "http://localhost:8080/jo/";
}
