const header = document.querySelector(".header");
const content2 = document.querySelector(".content2");
const content1 = document.querySelector(".content1");
const level = document.querySelector("#level");
const joins = document.querySelector("#joins");
const user_name = document.querySelector("#user_name");

//리프레쉬가토큰이 있으면 login user 정보를 받아온다.
//이름, 생일, Role, Grade
if(getCookie("refresh") !== null) {
	
	//정보를 잘 받아오는지 임시로 테스트 하는 부분 나중에 삭제할것
	const access = getCookie("access")? getCookie("access") : 100;
	
	//access와 refresh는 전부 쿠키에 담아서 보낸다
	//쿠키로 확인을 할 것이기 때문에 따로 header에 넣어 줘야 하는 부분은 없다.
	fetch("http://localhost:8080/jo/api/users",{
        method : 'POST',
        mode : 'cors',
        cache : 'no-cache',
        /////Content Type은 json으로 명시한다.
        headers: {'Content-Type': 'application/json', 'Authorization': access },
        credentials : 'same-origin',
        redirect : 'follow',
        referrer : 'no-referrer',
	})
	.then(response => response.json())
	.then((data) => {
		//사용자 정보가 잘 받아졌다. user가 존재한다.
		const userName = data.userName ? data.userName : "홍길동";
		const userBirth = data.userBirth ? true : false;
		const userRole = data.userRole ? true : false;
		const userGrade = data.userGrade ? data.userGrade : "GRADE";
		
		//login user에게는 header(홈로고, 로그아웃, 마이페이지)보여준다.
		header.classList.remove("hidden");
		//home contents를 보여주기
		content2.classList.remove("hidden");
		//login 버튼 숨겨주기
		content1.classList.add("hidden");
		
		//받아온 정보 text로 넣어주는 부분
		user_name.innerText = userName;

		if(userBirth) {
			document.querySelector("#gift_logo").classList.remove("hidden");
			document.querySelector("#gift_logo").addEventListener("click", onClick);
		}
		else document.querySelector("#gift_logo").classList.add("hidden");
		
		//joins회원 정보를 확인해서 joins 버튼을 보여준다.
		if(userRole) {
			joins.classList.remove("hidden");
			joins.addEventListener("click", onClick);
		}
		else joins.classList.add("hidden");
		
			
		//grade정보를 확인해서 grade 버튼을 보여준다.
		if(userGrade === "BRONZE") level.innerText = "B";
		else if(userGrade === "SILVER") level.innerText = "S";
		else if(userGrade === "GOLD") level.innerText = "G";
		
		//eventlisner을 등록한다.
		level.addEventListener("click", onClick);
		document.querySelector(".modal_close").addEventListener("click", offClick);
		document.querySelector("#logout").addEventListener("click", onDelete);
	
		//user가 존재하지 않는다
		})
		.catch((e) => {
			//쿠키를 전부 지워주고, /error를 가지고 redirect  "잘못된 접근입니다 다시 로그인해주세요."
			console.log("error", e);
			deleteCookie('refresh');
			deleteCookie('access');
			location.href = "http://localhost:8080/jo/?error=잘못된 접근입니다. 다시 로그인해주세요";
		});	
} 
else {//리프레쉬 토큰이 없으면 logout user = 카카오로 로그인하기 화면만 보여진다.
	header.classList.add("hidden");
	content2.classList.add("hidden");
	content1.classList.remove("hidden");
}