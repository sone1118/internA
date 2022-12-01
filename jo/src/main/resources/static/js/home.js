const header = document.querySelector(".header");
const content2 = document.querySelector(".content2");
const content1 = document.querySelector(".content1");
const level = document.querySelector("#level");
const joins = document.querySelector("#joins");
const user_name = document.querySelector("#user_name");

//리프레쉬가 있으면 로그인한 user
if(getCookie("refresh") !== null) {
	//유저 데이터를 요청한다.
	//const access = getCookie('access');
	const access = "7";
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
		console.log(data);
		if(data !== null) {
			const userName = data.userName ? data.userName : "홍길동";
			const userBirth = data.userBirth ? data.userBirth : "";
			const userRole = data.userRole ? data.userRole : "";
			const userGrade = data.userGrade;
			console.log(userBirth);
			header.classList.remove("hidden");
			content2.classList.remove("hidden");
			content1.classList.add("hidden");
			user_name.innerText = userName;
			
			let today = new Date();   
			let month = today.getMonth() + 1; 
			let date = today.getDate();  
			let compareDate = String(month) + "-" + String(date);
			
			console.log(compareDate);
			if(userBirth === compareDate) {
				document.querySelector("#gift_logo").classList.remove("hidden");
				document.querySelector("#gift_logo").addEventListener("click", onClick);
			}
			else document.querySelector("#gift_logo").classList.add("hidden");
			
			
			if(userRole === "JOINS") {
				joins.classList.remove("hidden");
				joins.addEventListener("click", onClick);
			}
			else joins.classList.add("hidden");
			
				
			if(userGrade === "BRONZE") level.innerText = "B";
			else if(userGrade === "SILVER") level.innerText = "S";
			else if(userGrade === "GOLD") level.innerText = "G";
			
			level.addEventListener("click", onClick);
			document.querySelector(".modal_close").addEventListener("click", offClick);
			document.querySelector("#logout").addEventListener("click", onDelete);
		}
		else {
			deleteCookie('refresh');
			deleteCookie('access');
			location.href = "http://localhost:8080/jo/";
			}
		}
	);	
} 
else {//리프레쉬가 없으면 logout user
	header.classList.add("hidden");
	content2.classList.add("hidden");
	content1.classList.remove("hidden");
}