function changeIcon(x) {
			x.classList.toggle("change");
		}

document.getElementById("comfirmation1").style.display = "none";


$('#go')
.click(
		function() {
			var inventoryTransferId = $(
					'#transactionId').val();
			var facilityId = $(
					'select[name="department_from"]')
					.val();
			var facilityIdTo = $(
					'select[name="department_to"]')
					.val();
			var statusId = $(
					'select[name="status"]')
					.val();
			var password = "ofbiz";
			var username = "admin";
			var data0 = {
				searchByInventoryTransferId : inventoryTransferId,
				searchByfacilityId : facilityId,
				searchByfacilityIdTo : facilityIdTo,
				searchBystatusId : statusId
			};
			
			var inventoryTransferSearch = $(
			'#inventoryTransferSearchUrl')
			.val();
			var json = JSON
					.stringify(data0);
			$
					.ajax({
						type : 'GET',
						url : ui_url+'filter/departmentTransferList',
						data : {
							json : json
							
						},
						dataType : 'json',
						success : function(
								data) {

							populateDataTable(data);

						}

					});
		});

$('#Create_New').click(function() {
var listProduct = $(
'#listProduct')
.val();
$.ajax({
type : 'GET',
url : listProduct,
data : {},
dataType : 'json',
success : function(data) {

	populateDataTable(data);
}

});
});

function populateDataTable(data) {
var table = $('#store-transfer').DataTable();
table.clear();
var result = JSON.parse(JSON.stringify(data));
for (var i = 0, l = result.length; i < l; i++) {
var obj = result[i];
var status = obj.statusId;
if (status == "IXF_COMPLETE") {
	obj.statusId = "Approved";
} else {
	obj.statusId = "Approval Pending";
}
$('#store-transfer')
		.dataTable()
		.fnAddData(
				[
						obj.inventoryTransferId,
						obj.facilityIdTo,
						obj.comments,
						obj.sendDate,
						obj.facilityId,
						obj.statusId,
						'<td ><a href="#"><img src="assets/images/print.png"></a></td>',
						'<td><a href="#"><img src="assets/images/button-view.png"></a></td>', ]);
}

}

var inventoryTransferId = "";
$('#myBody').on(
'click',
'tr',
function() {
	var table = $('#store-transfer')
			.DataTable();
	var staus = table.row(this).data()[5];
	
	if (staus == "Approved"
			|| staus == "<span>Approved</span>") {

	} else {
		inventoryTransferId = table.row(
				this).data()[0];
		openConfirmation();

	}
});

$('#approval').click(
function() {
	var inventoryTransferId = $(this)
			.closest('tr').find(
					'.inventoryTransferId')
			.text();

	openConfirmation();

});

$('#yes')
.click(
		function() {

			var transferComplete = $(
			'#transferCompleteUrl')
			.val();
			$
					.ajax({
						type : 'GET',
						url : ui_url+'transferInventory/complete',
						data : {
							inventoryTransferId : inventoryTransferId
							
						},
						dataType : 'json',
						success : function(data) {
							console
									.log(data);
							
//							if (data.message == "Success") {
//							
//							jQuery.noConflict();
//								$('#confirmationModal1').modal('show');
//						
//							
//							 
//						}
							
							if (data.message == "Success") {
								$('#confirmationModal1').modal('toggle');
								$(".modal").on("hidden.bs.modal", function () {
								    window.location = ui_url+'departmentTransferList';
								});
								
							} else {
								$('#alreadyExistModal1').modal('toggle');
								$("#msgtext").append(response.message);
							}
					}

		});

});


function openConfirmation() {
	document.getElementById("comfirmation1").style.display = "block";

}
function closeConfirmation() {
	document.getElementById("comfirmation1").style.display = "none";
}
function modalclose() {
	$('#confirmationModal1').modal('hide');
	window.location = ui_url+'departmentTransferList';
	

}
