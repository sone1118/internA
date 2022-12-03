/*
const userName = document.querySelector("#userName");
const userEmail = document.querySelector("#userEmail");
const userBirth = document.querySelector("#userBirth");
const userCreateAt = document.querySelector("#userCreateAt");
const userGrade = document.querySelector("#userGrade");
const userRole = document.querySelector("#joins");

//리프레쉬가토큰이 있으면 login user 정보를 받아온다.
//이름, email, birth, createAt, grade
if(getCookie("refresh")) {
	//로그인 유저에게는 홈로고, 로그아웃, 마이페이지가 보여야 한다.
	const header = document.querySelector(".header");
	if(header !== null) header.classList.remove("hidden");

	//정보를 잘 받아오는지 임시로 테스트 하는 부분 나중에 삭제할것
	const access = getCookie("access");
	
	//access와 refresh는 전부 쿠키에 담아서 보낸다
	//쿠키로 확인을 할 것이기 때문에 따로 header에 넣어 줘야 하는 부분은 없다.
	fetch("http://localhost:8080/jo/api/mypage",{
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
		//유저 데이터가 잘 받아졌다.
		//데이를 화면에 뿌린다.		
		const user_Name = data.userName ? data.userName : "홍길동";
		const user_Email = data.userEmail ? data.userEmail : "EMAIL";
		const user_Birth = data.userBirth ? data.userBirth : "99.99";
		const user_CreateAt = data.userCreateAt ? data.userCreateAt : "9999.99.99";
		const user_Grade = data.userGrade ? data.userGrade : "Grade";
		const user_Role = data.userRole ? true : false;
		
		userName.innerText = user_Name;
		userEmail.innerText = user_Email;
		userBirth.innerText = user_Birth;
		userCreateAt.innerText = user_CreateAt;
		userGrade.innerText = user_Grade;
		if(user_Role) userRole.classList.add("hidden");
		else userRole.classList.remove("hidden");
		
		//logout버튼에 event걸어주기
		logout.addEventListener("click", onDelete);			
	})
	.catch((error) => {
		//쿠키를 전부 지워주고, /error를 가지고 redirect "잘못된 접근입니다 다시 로그인해주세요."
		console.log("error", error);
		deleteCookie('refresh');
		deleteCookie('access');
		location.href = "http://localhost:8080/jo/?error=잘못된 접근입니다. 다시 로그인해주세요";
		
	});
} 
else { //리프레쉬 토큰이 없으면 logout user = 카카오로 로그인하기 화면만 보여진다.
	deleteCookie('refresh');
	deleteCookie('access');
	location.href = "http://localhost:8080/jo/";
}
*/
