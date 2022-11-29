function loginClick() {
//login클릭시 login으로 api요청
//값이 잘 들어오면 access 랑 refresh 저장하는 부분
/*
		fetch("http://localhost:8080/jo/api/login")
  			.then((response) => response.json())
  			.then((data) => {
				  //데이터가 잘들어옴
				  console.log(data)
				  		//if 쿠키가 왔음
				const token = {
					access: "asdf1234",
					refresh: "1111asdf",
				}
				//쿠키에 값 저장		
				setCookie('access', token.access, 1);
				setCookie('refresh', token.refresh, 1);	  
			})
			.catch((error) => {
				console.log("에러발생", error);
			});

	   location.href = "http://localhost:8080/jo/";
*/
	//임시로 값이 잘 들어 왔다고 넣어준 쿠키값
	setCookie('access', "asdf1234", 1);
	setCookie('refresh', "1111asdd", 1);
	location.href = "http://localhost:8080/jo/";
}

const header = document.querySelector(".header");
const content2 = document.querySelector(".content2");
const content1 = document.querySelector(".content1");
const login_button = document.querySelector("#login_button");
const level = document.querySelector("#level");
const joins = document.querySelector("#joins");
const user_name = document.querySelector("#user_name");

//리프레쉬가 있으면 로그인한 user
if(getCookie("refresh") !== null) {
	
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
	userBirth: "1129",
	userRole: "JOINS",
	userGrade: "BRONZE",
	userAgreeMarketing: true,
	userAgreeSns: true};
	
	header.classList.remove("hidden");
	content2.classList.remove("hidden");
	content1.classList.add("hidden");
	user_name.innerText = user.userName;
	
	let today = new Date();   
	let month = today.getMonth() + 1; 
	let date = today.getDate();  
	console.log(month, date);
	let compareDate = String(month) + String(date);
	console.log(compareDate);

	if(user.userBirth === compareDate) {
		document.querySelector("#gift_logo").classList.remove("hidden");
		document.querySelector("#gift_logo").addEventListener("click", onClick);
	}else {
		document.querySelector("#gift_logo").classList.add("hidden");
	}
	
	if(user.userRole === "JOINS") {
		joins.classList.remove("hidden");
		joins.addEventListener("click", onClick);
	}else {
		joins.classList.add("hidden");
	}
		
	if(user.userGrade === "BRONZE") level.innerText = "B";
	else if(user.userGrade === "SILVER") level.innerText = "S";
	else if(user.userGrade === "GOLD") level.innerText = "G";
	
	level.addEventListener("click", onClick);
	document.querySelector(".modal_close").addEventListener("click", offClick);
	document.querySelector("#logout").addEventListener("click", onDelete);
		
} else {//리프레쉬가 없으면 logout user
	header.classList.add("hidden");
	content2.classList.add("hidden");
	content1.classList.remove("hidden");
	login_button.addEventListener("click", loginClick);
}