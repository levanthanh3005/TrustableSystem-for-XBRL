$(document).ready(function() {
	$("#userForm").hide();

	$('body').on('click', '#configureBtn', function() {
		$("#userForm").show();
	});
	$('body').on('click', '#saveBtn', function() {
		userAddress = $("#userAddress").val();
		userPass = $("#userPass").val();
		console.log(">>>"+userAddress+">>"+userPass);
		if (!userAddress || !userPass){
			alert("Please enter your address and password");
			return;
		}
		$.ajax({
			url : "configureUserInfor",
			type : "POST",
			dataType : "json",
			data : {
				userAddress : userAddress,
				userPass : userPass
			}
		}).done(function(response) {
			$("#userForm").hide();
		}).fail(function(xhr, status, errorThrown) {
			console.log("Error: " + errorThrown);
			console.log("Status: " + status);
			console.dir(xhr);
		});
	});
})