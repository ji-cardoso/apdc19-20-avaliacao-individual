captureData = function(event) {
			    var data = $('form[name="removeOtherAccount"]').jsonify();
			    data.user_token = localStorage.getItem('user_token');
			    data.user = localStorage.getItem('user');

				console.log(data);
				$.ajax({
					type: "DELETE",
					url: "https://mimetic-surf-271517.appspot.com/rest/remove/other",
					contentType: "application/json; charset=utf-8",
					crossDomain: true,
					success: function(response) {
						alert("You've successfully deleted an account");
		                window.location.href = "/GBO.html"
					},
					error: function(response) {
						alert("Error: "+ response.status);
					},
					data: JSON.stringify(data)
				});

				event.preventDefault();
			};

			window.onload = function() {
			    var frms = $('form[name="removeOtherAccount"]');
			    frms[0].onsubmit = captureData;
			}
