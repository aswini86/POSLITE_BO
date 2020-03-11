
(function() {
	if (typeof message != 'undefined' && message) {
	    var msg = message;
	}else{
		return null;
	};
	
        var options = {
        		data: msg,
 			    getValue: function (element) {
 				    return element.contactNumber;
 				 },
 		   list: {
 			 match: {
 		            enabled: true
 		        },
 			onSelectItemEvent: function(){
 				var firstName = $("#contactNumber").getSelectedItemData().firstName;
 				var partyId = $("#contactNumber").getSelectedItemData().partyId;
 				
 				$("#customerName").val(firstName);
 				$("#customerId").val(partyId);
 			}
 		},

 		template: {
 			type: "custom",
 			method: function(value, item) {
 				if(value !== " "){
 					return  value;
 				}else{
 					return "<button class='btn btn-primary pull-right' data-toggle='modal' data-target='#searchCartModel'>Search Online</button>";
 				}
 			}
 		}
 	};

	$("#contactNumber").easyAutocomplete(options);
})();

function createCreditLimit () {
		
		var password = "ofbiz";
	    var partyId = $('#customerId').val();
	    var creditLimit = $('#creditLimit').val();
	    var username = $('#createdBy').val();
	    
	    var data0 = {
	    	partyId : partyId,
	    	creditLimit : creditLimit,
	    }
	    var json = JSON.stringify(data0);
	    $.ajax({
	      type : 'GET',
	      url : '/updateCreditLimit',
	      data : {
	        json : json,
	        username : username,
	        password : password,
	      },
	      dataType : 'json',
	      success : function(data) {
	    	  $('#closeConfirmationModal').modal('toggle');
	      },
	      error : function(data) {
			$('#alreadyExistModal').modal('toggle');
			$("#msgtext").append("Error while Creating Set Limit");
	      }
	      
	    });
	}
function modalredirect() {
	window.location = '/credit';
	$('#confirmationCloseModal').modal('hide');
}


