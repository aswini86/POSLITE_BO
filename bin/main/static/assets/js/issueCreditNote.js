
document.getElementById("approve1").style.display = "none";
document.getElementById("saveCloseConfirmation").style.display = "none";

function openForm() {
	document.getElementById("approve1").style.display = "block";
}
function closeForm() {
	document.getElementById("approve1").style.display = "none";
}
function closeSaveConfirmation() {
	document.getElementById("saveCloseConfirmation").style.display = "none";
}
function saveCloseConfirmation() {
	document.getElementById("saveCloseConfirmation").style.display = "block";
	document.getElementById("approve1").style.display = "none";
}

$("#issue_credit_list").on("change","tr", function () {
	const productIdArray = [];
	const returnQtyArray = [];
	const totalPriceArray = [];
	const mrpArray = [];
	
	var returnQty = "";
	var totalPrice = "";
	var productId = "";
	var totalAmt = "";
	var productMrp = "";
	var qty = "";
	
	$("#issue_credit_list input[type=checkbox]:checked").each(function () {
		var row = $(this).closest("tr")[0];
		productId = row.cells[0].childNodes[0].value;
		returnQty = row.cells[3].childNodes[0].value;
		qty = row.cells[2].innerHTML;
		totalPrice = row.cells[5].innerHTML;
		productMrp = row.cells[5].innerHTML;
		
		totalAmt = Math.round((parseFloat(returnQty)*parseFloat(totalPrice)));
		//row.cells[9].innerHTML = ratePercentage;
		totalPriceArray.push(totalAmt);
		productIdArray.push(productId);
		returnQtyArray.push(returnQty);
		mrpArray.push(productMrp);
		
		if (parseFloat(returnQty) <= parseFloat(qty)){
			openForm();
		}
		if(parseFloat(returnQty) > parseFloat(qty)) {
			$('#quantityHighModal').modal('toggle');
	        $("#msgtext").append("Return Quantity should be less than Quantity");
	        document.getElementById("approve1").style.display = "none";
	        return false;
		} 
	});
	$("#productIdArray").val(productIdArray);
	$("#totalPriceArray").val(totalPriceArray);
	$("#returnQtyArray").val(returnQtyArray);
	$("#productMrpArray").val(mrpArray);
	
	
});

$('#savecloseyes').click(
		function() {
			productIdArray = $('#productIdArray').val();
			totalPriceArray = $('#totalPriceArray').val();
			returnQtyArray = $('#returnQtyArray').val();
			productMrpArray = $('#productMrpArray').val();
			receiptId = $('#receiptId').val();
			customerId = $('#customerId').val();
            type = "CREDIT_NOTE";
            
			var data0 = {
				productIdArray : productIdArray,
				totalPriceArray : totalPriceArray,
				returnQtyArray : returnQtyArray,
				productMrpArray : productMrpArray,
				receiptId : receiptId,
				type : type,
				customerId : customerId,
			}
			var json = JSON.stringify(data0);
			$.ajax({
				type : 'GET',
				url : '/issueCreditNoteToCustomer',
				data : {
					json : json,
					username : "admin",
					password : "ofbiz",
				},
				dataType : 'json',
				success : function(data) {
                    if (data != "" && data != null) {
                        $('#confirmationModal').modal('toggle');
                        $("#successmsgtext").append("Credit Note issued Successfully!");
                    } else {
                        $('#alreadyExistModal').modal('toggle');
                        $("#msgtext").append("Error while Issuing Credit Note");
                    }
				}
			});
			$('#redirect').click(
				function() {
					var homeUrl = '/PosbillSummary';
					window.location = homeUrl;
			});
});

