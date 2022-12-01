//리플레쉬가 있으면 로그인된 user
if(getCookie("refresh")) {
	const header = document.querySelector(".header");
	if(header !== null) header.classList.remove("hidden");

	//const access = getCookie('access');
	const access = "4";
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
		console.log(data);
		if(data !== null) {
			const userName = document.querySelector("#userName");
			const userEmail = document.querySelector("#userEmail");
			const userBirth = document.querySelector("#userBirth");
			const userCreateAt = document.querySelector("#userCreateAt");
			const userGrade = document.querySelector("#userGrade");
			
			const user_Name = data.userName ? data.userName : "홍길동";
			const user_Email = data.userEmail ? data.userEmail : "";
			//const user_Birth = data.userBirth ? data.userBirth : "";
			//const user_CreateAt = data.userCreateAt ? data.userCreateAt : "";
			const user_Grade = data.userGrade;
			
			userName.innerText = user_Name;
			userEmail.innerText = user_Email;
			//userBirth.innerText = "**.**";
			//userCreateAt.innerText = "****.**.**";
			userGrade.innerText = user_Grade;			
		}
		else {
			console.log("잘못된 접근입니다");
			deleteCookie('refresh');
			deleteCookie('access');
			location.href = "http://localhost:8080/jo/";
			}
		}
	)
	.catch((error) => console.log(error));	

	//화면에 값을 넣어 뿌림
	//userName.innerText = user.userName;
	//let [id, email] = user.userEmail.split("@");
	//let regex = /[A-Za-z0-9]/g;
	//id = id.replace(regex, "*");
	//userEmail.innerText = id + "@" + email;
	//userBirth.innerText = "****.**.**";
	//userCreateAt.innerText = user.userBirth;
	//userGrade.innerText = user.userGrade;
	logout.addEventListener("click", onDelete);

} else {
	location.href = "http://localhost:8080/jo/";
}
