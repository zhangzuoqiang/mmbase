/* -*- mode: java -*- */
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><mm:content  expires="0" type="text/javascript">
    <mm:cloud>

// hmm

function toggle(number) {
    $(".toggle_div" + number + ",#toggle_div" + number).toggle();
    var img = $("#toggle_image" + number)[0];
    var org = img.src;
    img.src = img.src.replace("minus", "plus");
    if (img.src == org) {
	img.src = img.src.replace("plus", "minus");
    }
}

$(document).ready(function() {
        $(".col.problem").click(function() {
                $(document).find(".col.problem .extra").hide();
                $(this).find(".extra").show();
            });
    });

</mm:cloud>
</mm:content>
