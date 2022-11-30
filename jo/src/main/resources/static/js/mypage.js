function onDelete() {
	deleteCookie("refresh");
	deleteCookie("access");
	location.href = "http://localhost:8080/jo/";
}

//리플레쉬가 있으면 로그인된 user
if(getCookie("refresh")) {
	const header = document.querySelector(".header");
	//const userName = document.querySelector("#userName");
	//const userEmail = document.querySelector("#userEmail");
	//const userBirth = document.querySelector("#userBirth");
	//const userCreateAt = document.querySelector("#userCreateAt");
	//const userGrade = document.querySelector("#userGrade");
	//const logout = document.querySelector("#logout");
	
	if(header !== null) header.classList.remove("hidden");

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
