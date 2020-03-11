
function changeIcon(x) {
	x.classList.toggle("change");
}

function closeForm() {
	document.getElementById("approve1").style.display = "none";
}

function openConfirmation() {
	document.getElementById("comfirmation1").style.display = "block";
	document.getElementById("approve1").style.display = "none";
}
function closeConfirmation() {
	document.getElementById("comfirmation1").style.display = "none";
}

function approveConfirmation() {
	document.getElementById("approveComfirmation").style.display = "block";
	document.getElementById("approve1").style.display = "none";
}
function closeApproveConfirmation() {
	document.getElementById("approveComfirmation").style.display = "none";
}

function saveCloseConfirmation() {
	document.getElementById("saveCloseConfirmation").style.display = "block";
	document.getElementById("approve1").style.display = "none";
}
function closeSaveConfirmation() {
	document.getElementById("saveCloseConfirmation").style.display = "none";
}

$(document).ready(
		function() {
			document.getElementById("approve1").style.display = "none";
			document.getElementById("comfirmation1").style.display = "none";
			document.getElementById("approveComfirmation").style.display = "none";
			document.getElementById("saveCloseConfirmation").style.display = "none";

			$('#go').click(
				function() {
					var type = $('#searchByProductName').val();
					var department_from = $('#department_from').val();
					var findInventoryItemUrl = $('#findInventoryItemUrl').val();
					$.ajax({
							type : 'GET',
							url : findInventoryItemUrl,
							data : {
								type : type,
								department_from : department_from
							},
							dataType : 'json',
							success : function(
									data) {

								populateDataTable(data);

							}

						});
				});
			
			var itemId = "";
			var transferQnty = "";
			var quantity = "";
			var facilityId = "";
			var facilityIdTo = "";
			var password = "";
			var username = "";
			var comments = "";
			var sendDate = "";
			var receiveDate = "";

			$('.result1').click(function() {
					itemId = $(this).closest('tr').find('.itemId').text();
					var rowIndex = $(this).closest('tr').index();
					var currentRow = $(this).closest("tr");
					transferQnty = currentRow.find("td:eq(8)").text();
					quantity = transferQnty;
					console.log(quantity);
					facilityId = $('select[name="department_from"]').val();
					facilityIdTo = $('select[name="department_to"]').val();
					comments = $('#comments').val();
					sendDate = $('#transfer_date').val();
					receiveDate = $('#transfer_date').val();
					if (quantity != "") {
						openForm();
					}

			});
			
	$('#yes').click(function() {
				var data0 = {
					inventoryItemId : itemId,
					xferQty : quantity,
					statusId : "IXF_REQUESTED",
					facilityId : facilityId,
					facilityIdTo : facilityIdTo,
					comments : comments,
					sendDate : sendDate,
					receiveDate : receiveDate
				};
				var inventoryTransferUrl = $('#inventoryTransferUrl').val();
				var json = JSON.stringify(data0);
				$.ajax({
					type : 'GET',
					url : inventoryTransferUrl,
					data : {
						json : json,
						password : "ofbiz",
						username : "admin"
					},
					dataType : 'json',
					success : function(data) {
						if (data != "" && data != null) {
							//jQuery.noConflict();
							$('#confirmationModal').modal('show');
						} else {
							$('#alreadyExistModal').modal('toggle');
							$("#msgtext").append("Error while raising Stock Transfer");
						}
					},
					error : function(data) {
						//jQuery.noConflict();
						$('#alreadyExistModal').modal('toggle');
						$("#msgtext").append("Error while raising Stock Transfer");
					}

				});

			});
	
	$('#approveyes').click(function() {
		var data0 = {
			inventoryItemId : itemId,
			xferQty : quantity,
			statusId : "IXF_REQUESTED",
			facilityId : facilityId,
			facilityIdTo : facilityIdTo,
			comments : comments,
			sendDate : sendDate,
			receiveDate : receiveDate
		};
		var inventoryTransferUrl = $('#inventoryTransferUrl').val();
		var json = JSON.stringify(data0);
		$.ajax({
			type : 'GET',
			url : inventoryTransferUrl,
			data : {
				json : json,
				password : "ofbiz",
				username : "admin"
			},
			dataType : 'json',
			success : function(data) {
				if (data != "" && data != null) {
					jQuery('#approveConfirmationModal').modal('show');
				} else {
					$('#alreadyExistModal').modal('toggle');
					$("#msgtext").append("Error while raising Stock Transfer");
				}
			},
			error : function(data) {
				$('#alreadyExistModal').modal('toggle');
				$("#msgtext").append("Error while raising Stock Transfer");
			}

		});

	});
	
	$('#savecloseyes').click(function() {
		var data0 = {
			inventoryItemId : itemId,
			xferQty : quantity,
			statusId : "IXF_REQUESTED",
			facilityId : facilityId,
			facilityIdTo : facilityIdTo,
			comments : comments,
			sendDate : sendDate,
			receiveDate : receiveDate
		};
		var inventoryTransferUrl = $('#inventoryTransferUrl').val();
		var json = JSON.stringify(data0);
		$.ajax({
			type : 'GET',
			url : inventoryTransferUrl,
			data : {
				json : json,
				password : "ofbiz",
				username : "admin"
			},
			dataType : 'json',
			success : function(data) {
				if (data != "" && data != null) {
					$('#closeConfirmationModal').modal('toggle');
				} else {
					$('#alreadyExistModal').modal('toggle');
					$("#msgtext").append("Error while raising Stock Transfer");
				}
			},
			error : function(data) {
				$('#alreadyExistModal').modal('toggle');
				$("#msgtext").append("Error while raising Stock Transfer");
			}

		});

	});
	
	
		function openForm() {
			document.getElementById("approve1").style.display = "block";

		}
		function confirmCloseform() {
			document.getElementById("comfirmation1").style.display = "block";
		}
		function populateDataTable(data) {
			var table = $('#interdependent_transfer').DataTable();
			table.clear();
			var result = JSON.parse(JSON.stringify(data));
			for (var i = 0, l = result.length; i < l; i++) {
				var obj = result[i];
				$('#interdependent_transfer').dataTable().fnAddData([
					'<span class="delete-span"><i class="fa fa-trash"></i></span',
					obj.ean,
					obj.inventoryItemId,
					obj.productId,
					'<td><select name="unit_id" class="table-select"><option>EA</option></select></td>',

					obj.productName,
					obj.lotId,
					'<td class="transfer" contentEditable="true"></td>',
					obj.quantityOnHandTotal,
					obj.quantityOnHandTotal,
					obj.unitCost,
					obj.serialNumber,
					obj.expireDate,
					'<input type="text" name="" value='+obj.inventoryItemId+'>']);
			}
		}

		$('#myBody').on('click','tr',
			function() {
				var table = $('#interdependent_transfer').DataTable();
				var currentRow = table.row(this).data()[4];
				itemId = table.row(this).data()[2];
				transferQnty = table.row(this).data()[8];
				quantity = transferQnty;
				facilityId = $('select[name="department_from"]').val();
				facilityIdTo = $('select[name="department_to"]').val();
				comments = $('#comments').val();
				sendDate = $('#transfer_date').val();
				receiveDate = $('#transfer_date').val();
				openForm();
			});

		var myVar = setInterval(myTimer, 1000);

		function myTimer() {
			var d = new Date();
			var t = d.toLocaleString();
			document.getElementById("demo").innerHTML = t;
		}
	});

function modalclose() {
	window.location = '/listProduct';
	$('#confirmationModal').modal('hide');
}

function modalredirect() {
	window.location = '/storetransferoutlist';
	$('#confirmationCloseModal').modal('hide');
}
