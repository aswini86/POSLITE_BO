/*Stock adjustment List

json datatable dummy data */
console.log("testing");

var transaction_data = [ {
	"transactionId" : "890208040491",
	"remarks" : "testing",
	"user" : "Admin",
	"date" : "11/08/2018",
	"status" : "Approved"
}, {
	"transactionId" : "890208040492",
	"remarks" : "testing",
	"user" : "Admin1",
	"date" : "11/08/2018",
	"status" : "pending"
}, {
	"transactionId" : "890208040493",
	"remarks" : "testing",
	"user" : "Admin",
	"date" : "11/08/2018",
	"status" : "Approved"
}, {
	"transactionId" : "890208040494",
	"remarks" : "testing",
	"user" : "Admin",
	"date" : "11/08/2018",
	"status" : "pending"
}, {
	"transactionId" : "890208040495",
	"remarks" : "testing",
	"user" : "Admin",
	"date" : "11/08/2018",
	"status" : "Approved"

} ];
/*json datatable dummy data end*/ 

function searchData(data, transID="", status1="") {
	var resultArr = [];
	for (var i = 0; i < data.length; i++) {
		var obj = data[i];
		//console.log(obj);
		if(status1){
			console.log("selected status", status1, "status",obj['status']);
			   if (obj['status'] == status1) {
				   console.log("status", obj['status']);
					resultArr.push(obj);
				    }else{
					 console.log("if data Not available");
				    } 
			     } else if(transID) {
				  if(obj['transactionId'] == transID){
					  console.log("transID", obj['transactionId']);
					   resultArr.push(obj);
				       }else{
					    console.log("else if data Not available");
				      }
			     } else{
			       console.log("No data available");
		         }
     }
	return resultArr;
    }

function showOptions(s) {
	  //console.log(s[s.selectedIndex].value); // get value
	  //console.log(s[s.selectedIndex].id); // get id
	}

var options = {
	data : [ {
		name : "id 1",
		type : "890208040491",
		icon : "transport/2"
	}, {
		name : "id 2",
		type : "890208040492",
		icon : "transport/10"
	}, {
		name : "id 3",
		type : "890208040493",
		icon : "transport/1"
	}, {
		name : "id 4",
		type : "890208040494",
		icon : "/transport/11"
	}, {
		name : "id 5",
		type : "890208040495",
		icon : "/transport/11"
	}, {
		name : "transaction id is not in your list?",
		type : "transaction",
		icon : ""
	} ],

	getValue : "type",

	template : {
		type : "custom",
		method : function(value, item) {
			if (item.icon !== "") {
				return item.type;
			}
		}
	}
};

$("#search").easyAutocomplete(options);

/*render table data*/
$("#search_transaction").submit( function(e) {
					e.preventDefault();
					var status= $("#status option:selected").text();
					var data = searchData(transaction_data, search.value, status);
					console.log(data);
					
					$("#stockadjustmentlist")
							.DataTable(
									{
										scrollY : 300,
										scrollX : true,
										scrollCollapse : true,
										paging : false,
										destroy: true,
										searching: false,
										ordering : false,
										bInfo : false,

										fixedColumns : {
											leftColumns : 0
										},

										select : {
											style : 'os',
											selector : 'td:first-child'
										},
										"aaData" : [ [
											data[j].transactionId,
											data[j].remarks,
											data[j].user,
											data[j].date,
											data[j].status,
												'<a href="#"><img src="assets/images/print.png" width="24"></a>',
												'<a href="#"><img src="assets/images/button-view.png" width="24"></a>' ] ],
									});
					
				});


/*Stock adjustment List end
 ******************************** 
***** Stock adjustment start ***** */

/*  json datatable dummy data  */

var productEAN = document.getElementById("search_article");
$("#product_search").click( function() {
console.log(productEAN.value);
var data = searchProduct(product_data, search_article.value);
$("#stockadjustment")
		.DataTable(
				{
					scrollY : 300,
					scrollX : true,
					scrollCollapse : true,
					paging : false,
					destroy: true,
					searching: false,
					ordering : false,
					bInfo : false,

					fixedColumns : {
						leftColumns : 0
					},

					select : {
						style : 'os',
						selector : 'td:first-child'
					},
					"aaData" : [ [
						data[0].EANCode,
						data[0].uom,
						data[0].itemDescription,
						data[0].Reason,
						data[0].Receipt,
						data[0].packs,
						data[0].packSize,
						data[0].quantity,
						data[0].mrp,
						data[0].saleRate,
						data[0].coBatchNo,
						data[0].expiryDate ] ],
				});

});

function searchProduct(data, criteria) {
	var resultArr = [];
	for (var i = 0; i < data.length; i++) {
		var obj = data[i];
		if (obj['transactionId'] == criteria) {
			resultArr.push(obj);
		}
	}
	return resultArr;
}	   

var product_data = [ {
	"EANCode" : "123451",
	"uom" : "testing",
	"itemDescription" : "Admin",
	"Reason" : "11/08/2018",
	"Receipt" : "Approved",
	"packs" : "546848647",
	"packSize" : "testing",
	"quantity" : "Admin",
	"mrp" : "11/08/2018",
	"saleRate" : "Approved",
	"coBatchNo" : "11/08/2018",
	"expiryDate" : "Approved"
}, {
	"EANCode" : "123452",
	"uom" : "testing",
	"itemDescription" : "Admin",
	"Reason" : "11/08/2018",
	"Receipt" : "Approved",
	"packs" : "546848647",
	"packSize" : "testing",
	"quantity" : "Admin",
	"mrp" : "11/08/2018",
	"saleRate" : "Approved",
	"coBatchNo" : "11/08/2018",
	"expiryDate" : "Approved"
}, {
	"EANCode" : "123453",
	"uom" : "testing",
	"itemDescription" : "Admin",
	"Reason" : "11/08/2018",
	"Receipt" : "Approved",
	"packs" : "546848647",
	"packSize" : "testing",
	"quantity" : "Admin",
	"mrp" : "11/08/2018",
	"saleRate" : "Approved",
	"coBatchNo" : "11/08/2018",
	"expiryDate" : "Approved"
}, {
	"EANCode" : "123454",
	"uom" : "testing",
	"itemDescription" : "Admin",
	"Reason" : "11/08/2018",
	"Receipt" : "Approved",
	"packs" : "546848647",
	"packSize" : "testing",
	"quantity" : "Admin",
	"mrp" : "11/08/2018",
	"saleRate" : "Approved",
	"coBatchNo" : "11/08/2018",
	"expiryDate" : "Approved"
}, {
	"EANCode" : "123455",
	"uom" : "testing",
	"itemDescription" : "Admin",
	"Reason" : "11/08/2018",
	"Receipt" : "Approved",
	"packs" : "546848647",
	"packSize" : "testing",
	"quantity" : "Admin",
	"mrp" : "11/08/2018",
	"saleRate" : "Approved",
	"coBatchNo" : "11/08/2018",
	"expiryDate" : "Approved"

} ];
/*		getValue: "name",


		template: {
			type: "custom",
			method: function(value, item) {
				if(item.type !== ""){
					return  item.name;
				}
			}
		}
	};

	$("#search_article").easyAutocomplete(option);
	
	$("#product_search").click( function() {
	
		var data = searchData(product_data, search_article.value);
		console.log("testing", product_data);
	});
	
	var product_data = [ {
		"product" : "Laptop",
		"remarks" : "testing",
		"user" : "Admin",
		"date" : "11/08/2018",
		"status" : "Approved",
		"transactionId" : "546848647",
		"huhui" : "testing",
		"uzdxcser" : "Admin",
		"datdce" : "11/08/2018",
		"stadftus" : "Approved"
	}, {
		"product" : "Laptop",
		"remarks" : "testing",
		"user" : "Admin",
		"date" : "11/08/2018",
		"status" : "Approved",
		"transactionId" : "546448647",
		"huhui" : "testing",
		"uzdxcser" : "Admin",
		"datdce" : "11/08/2018",
		"stadftus" : "Approved"
	}, {
		"product" : "Keyboard",
		"remarks" : "testing",
		"user" : "Admin",
		"date" : "11/08/2018",
		"status" : "Approved",
		"transactionId" : "546788647",
		"huhui" : "testing",
		"uzdxcser" : "Admin",
		"datdce" : "11/08/2018",
		"stadftus" : "Approved"
	}, {
		"product" : "Keyboard",
		"remarks" : "testing",
		"user" : "Admin",
		"date" : "11/08/2018",
		"status" : "Approved",
		"transactionId" : "5875788647",
		"huhui" : "testing",
		"uzdxcser" : "Admin",
		"datdce" : "11/08/2018",
		"stadftus" : "Approved"
	}, {
		"product" : "Keyboard",
		"remarks" : "testing",
		"user" : "Admin",
		"date" : "11/08/2018",
		"status" : "Approved",
		"transactionId" : "545788647",
		"huhui" : "testing",
		"uzdxcser" : "Admin",
		"datdce" : "11/08/2018",
		"stadftus" : "Approved"

	} ];
*/