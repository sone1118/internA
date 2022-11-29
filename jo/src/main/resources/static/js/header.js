function onDelete() {
	deleteCookie("refresh");
	deleteCookie("access");
	location.href = "http://localhost:8080/jo/";
}

if(getCookie("refresh")) {
	const header = document.querySelector(".header");
	const logout = document.querySelector("#logout");
	if(header !== null) header.classList.remove("hidden");
	logout.addEventListener("click", onDelete);

}else {
	document.querySelector(".header").classList.add("hidden");
}