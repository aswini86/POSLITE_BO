$(document).ready(function () {
   
    $('#mapped-items').DataTable({
        paging: true,
        searching: true,
        ordering: true,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
 
    
    $('#unmapped-items').DataTable({
        paging: true,
        searching: true,
        ordering: true,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    
    $('#products').DataTable({
        paging: true,
        searching: true,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    
    $('#uploaded-items').DataTable({
        paging: false,
        searching: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    
    $('#scan-items').DataTable({
        paging: false,
        searching: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });

    $('#add-article').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */
        columnDefs: [{
                orderable: false,
                className: 'select-checkbox',
                targets: 0
            }],
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });

    $('#department-transfer').DataTable({
        scrollY: 450,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]

    });
    
    $('#store-transfer').DataTable({
        scrollY: 450,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    
    $('#selectArticleTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    $('#stockadjustment').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    $('#rateadjustment').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    $('#pos_cart').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    $('#selectReasonTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        //order: [[ 1, 'asc' ]]
    });


    $('#missingArticles').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
    });
    $('#selectarticle').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
    });
    $('#stockcheck').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
    });
    $('#articles').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        select: {
            style: 'os',
            selector: 'td:first-child'
        },
    });

    $('#VendorTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        //order: [[ 1, 'asc' ]]
    });
    /*$('#vendorItemMapping').DataTable( {
     scrollY:        400,
     scrollX:        true,
     scrollCollapse: true,
     paging:         false,
     ordering:        false,
     bInfo :          false,
     
     fixedColumns:   {
     leftColumns: 2
     }, 
     
     select: {
     style:    'os',
     selector: 'td:first-child'
     },
     //order: [[ 1, 'asc' ]]
     } );*/

    $('#purchaseOrderTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        bInfo: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        //order: [[ 1, 'asc' ]]
    });
    $('#marginTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        //order: [[ 1, 'asc' ]]
    });
    $('#productRecieveInfoTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        ordering: false,
        bInfo: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        //order: [[ 1, 'asc' ]]
    });
    $('#purchaseOutwardTable').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        bInfo: false,
        ordering: false,

        /*  fixedColumns:   {
         leftColumns: 2
         }, */
        select: {
            style: 'os',
            selector: 'td:first-child'
        },

    });
    $('#pos_summary').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
    $('#pos_summary_list').DataTable({
    	 paging: true,
         searching: true,
         ordering: true,
         select: {
             style: 'os',
             selector: 'td:first-child'
         },
         order: [[1, 'asc']]
    });
    $('#issue_credit_list').DataTable({
        scrollY: 400,
        scrollX: true,
        scrollCollapse: true,
        paging: true,
        ordering: false,
        /*  fixedColumns:   {
         leftColumns: 2
         }, */

        select: {
            style: 'os',
            selector: 'td:first-child'
        },
        order: [[1, 'asc']]
    });
});
