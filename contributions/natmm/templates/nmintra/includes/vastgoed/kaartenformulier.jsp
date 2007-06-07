<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud>

<html>
<head>
<title>bestelformulier plotopdrachten</title>


<bean:define id="natGebMap" property="natGebMap" name="KaartenForm" type="java.util.Map" />
<bean:define id="gebiedMap" property="gebiedMap" name="KaartenForm" type="java.util.Map" />
<bean:define id="selKaartMap" property="selKaartMap" name="KaartenForm" type="java.util.Map" />
<SCRIPT LANGUAGE="JavaScript">
<!--
arr_NatGeb = new Array
(
 <%
      Set firstKeySet = natGebMap.keySet();
      Iterator firstIterator = firstKeySet.iterator();
      while (firstIterator.hasNext()) {
     	out.println("new Array (");    
         String firstKey = (String) firstIterator.next();
         Map valuesMap = (TreeMap) natGebMap.get(firstKey);
         Set valuesKeySet = valuesMap.keySet();
         Iterator valuesIterator = valuesKeySet.iterator();
         int index = 0;
         while (valuesIterator.hasNext()) {
         	index++;
         	String valuesKey = (String) valuesIterator.next();
         	Boolean isSelected = (Boolean) valuesMap.get(valuesKey);
         	
         	out.print("new Array('" + valuesKey + "', " + index + ", " + isSelected + ")");
            if (valuesIterator.hasNext()) {
         		out.println(",");
        	}
         } 
         out.print(" ) ");
         if (firstIterator.hasNext()) {
         	out.println(",");
         }
      }   
%>
);

arr_Areaal = new Array
(
 <%
      firstKeySet = gebiedMap.keySet();
      firstIterator = firstKeySet.iterator();
      while (firstIterator.hasNext()) {
     	out.println("new Array (");    
         String firstKey = (String) firstIterator.next();
         Map valuesMap = (TreeMap) gebiedMap.get(firstKey);
         Set valuesKeySet = valuesMap.keySet();
         Iterator valuesIterator = valuesKeySet.iterator();
         int index = 0;
         while (valuesIterator.hasNext()) {
         	index++;
         	String valuesKey = (String) valuesIterator.next();
         	Boolean isSelected = (Boolean) valuesMap.get(valuesKey);
         	
         	out.print("new Array('" + valuesKey + "', " + index + ", " + isSelected + ")");
            if (valuesIterator.hasNext()) {
         		out.println(",");
        	}
         } 
         out.print(" ) ");
         if (firstIterator.hasNext()) {
         	out.println(",");
         }
      }   
%>
);

-->
</script>


<SCRIPT LANGUAGE="JavaScript">
<!--

function jsc_ClearArea(selectCtrl)
{
	var i;
	// leeg de lijst
	for (i = selectCtrl.options.length; i >= 0; i--)
		{
			selectCtrl.options[i] = null; 
		}
}

function jsc_VulSelectUitArray(selectCtrl, itemArray)
{
	var i, j;
	// leeg de tweede lijst
	for (i = selectCtrl.options.length; i >= 0; i--)
		{
			selectCtrl.options[i] = null; 
		}
		
	if (itemArray != null)
	{
		j = 0;
	}
	else
	{	
		j = 0;}
		if (itemArray != null)
		{
		// nieuwe items toevoegen
		for (i = 0; i < itemArray.length; i++)
		{
			selectCtrl.options[j] = new Option(itemArray[i][0], itemArray[i][0], true, itemArray[i][2]);
			if (itemArray[i][0] != null)
				{
					selectCtrl.options[j].value = itemArray[i][0]; 
				}
			j++;
		}
	// eerste item selecteren voor tweede lijst, is nu uitgeschakeld
	//selectCtrl.options[0].selected = true;
	}
}

function jsc_GeefInfo(id_DIV)
//DIV zichtbaar -> maak onzichtbaar, DIV onzichtbaar -> maak zichtbaar
{
	if (id_DIV.style.display=="none"){id_DIV.style.display=""}
	else{id_DIV.style.display="none"}
}

function small_window(NaamPagina) {
var newWindow;
var props = 'scrollBars=no,resizable=no,toolbar=no,status=0,minimize=no,statusbar=0,menubar=no,directories=no,width=screen.availWidth,height=screen.availHeight, top='+(20)+',left='+(20);
var fullLink;
for (var i = 0; i < document.forms[0].sel_Kaart.length; i++) {
	if (document.forms[0].sel_Kaart[i].selected) {
		var kartNode = document.forms[0].sel_Kaart[i].value;
		fullLink = NaamPagina + "?node=" + kartNode;
		var windowName = kartNode;
		newWindow = window.open(fullLink, windowName, props);
	}
}
newWindow.focus();
}

function jsc_defaultOptie() {
  <logic:equal name="KaartenForm" property="rad_Gebied" value="Natuurgebied(en)">
  	jsc_optie0();
  </logic:equal>
    <logic:equal name="KaartenForm" property="rad_Gebied" value="Eenheid">
  	jsc_optie1();
  </logic:equal>
    <logic:equal name="KaartenForm" property="rad_Gebied" value="Nederland">
  	jsc_optie2();
  </logic:equal>
  <logic:equal name="KaartenForm" property="rad_Gebied" value="Coordinaten">
  	jsc_optie3();
  </logic:equal>
}

var imagesNat = new Array();
var imagesEen = new Array();
var imagesNede = new Array();
var imagesCoor = new Array();

function jsc_optie0()
{
jsc_VulSelectUitArray(document.forms[0].sel_NatGeb, arr_NatGeb[document.forms[0].sel_Beheereenheden.selectedIndex]);
//jsc_ClearArea(document.forms[0].sel_Areaal);

<% ArrayList kartTypes = (ArrayList) selKaartMap.get("Natuurgebied(en)"); %>

document.forms[0].sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied == 'Natuurgebied(en)'">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
		document.forms[0].sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=fieldName%>", true, <%= kartTypes.contains(fieldName)%>);
	</mm:field>
	</mm:field>
	//
// APPLY TO ALL !!!
		imagesNat[<mm:index/>-1] = "http://localhost:8080/natmm-intranet/nmintra/media/vastgoed/Nicolao%20Visscher.jpg";  //this will come from the DB 
</mm:listnodes>
}

function jsc_optie1()
{
jsc_VulSelectUitArray(document.forms[0].sel_Areaal, arr_Areaal[document.forms[0].sel_gebieden.selectedIndex]);
//jsc_ClearArea(document.forms[0].sel_NatGeb);

<% kartTypes = (ArrayList) selKaartMap.get("Eenheid"); %>

document.forms[0].sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied == 'Eenheid'">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
		document.forms[0].sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=fieldName%>", true, <%= kartTypes.contains(fieldName)%>);
	</mm:field>
	</mm:field>
</mm:listnodes>
}

function jsc_optie2()
{
<% kartTypes = (ArrayList) selKaartMap.get("Nederland"); %>

document.forms[0].sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied == 'Nederland'">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
		document.forms[0].sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(fieldName)%>);
	</mm:field>
	</mm:field>
</mm:listnodes>
}

function jsc_optie3()
{
<% kartTypes = (ArrayList) selKaartMap.get("Coordinaten"); %>

document.forms[0].sel_Kaart.length=0;
<mm:listnodes type="thema_plot_kaart" constraints="type_gebied == 'Coordinaten'">
	<mm:field name="naam" jspvar="fieldName" write="false" vartype="String" >
	<mm:field name="number" jspvar="nodeNumber" write="false" vartype="String" >
		document.forms[0].sel_Kaart[<mm:index/>-1] =new Option("<%=fieldName%>", "<%=nodeNumber%>", true, <%= kartTypes.contains(fieldName)%>);
	</mm:field>
	</mm:field>
</mm:listnodes>
}

function jsc_setPicture(selectedIndex) {
	// use Natuurgebied(en)
	if(document.forms[0].rad_Gebied[0].checked) {
		document.getElementById("kartPicture").src=imagesNat[selectedIndex]; 
 	}
 	// use Eenheid
	if(document.forms[0].rad_Gebied[1].checked) {
		document.getElementById("kartPicture").src=imagesEen[selectedIndex]; 
 	}
 	// use Nederland
	if(document.forms[0].rad_Gebied[2].checked) {
		document.getElementById("kartPicture").src=imagesNede[selectedIndex]; 
 	}
 	// use Coordinaten
	if(document.forms[0].rad_Gebied[3].checked) {
		document.getElementById("kartPicture").src=imagesCoor[selectedIndex]; 
 	} 	
}

-->
</script>

<style type="text/css">
<!--

DIV.Pagina
{
	position: absolute;
	z-index:3;
}

DIV.Info
{
	position: relative;
	Color: #CC0033;
	cursor: hand;
	width: 29px;
	height: 24px;
	background-image: url(../../media/vastgoed/Info.png);
}

DIV.Schermuitleg 
{
	position: relative;
	height: auto;
	width: 470px;
	margin-top: .6em;
	margin-right: 3em;
	margin-left: 0;
	margin-bottom: .6em;
	padding-top: .75em;
	padding-right: 6px;
	padding-left: .75em;
	padding-bottom: .75em;
}


-->
</style>
</head>

<body onload="jsc_defaultOptie(); jsc_VulSelectUitArray(document.forms[0].sel_NatGeb, arr_NatGeb[document.forms[0].sel_Beheereenheden.selectedIndex]); jsc_VulSelectUitArray(document.forms[0].sel_Areaal, arr_Areaal[document.forms[0].sel_gebieden.selectedIndex]);">
<html:form action="/nmintra/includes/vastgoed/KaartenAction" method="GET">

	<table>
		<tr>
			<td width="450">
				Selecteer het(de) gebied(en) of geef de co�rdinaten:
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">

				</a>
			</td>
		</tr> 
	</table>	


	<table width ="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied" value="Natuurgebied(en)" onclick="jsc_optie0();"/>
			</td>
			<td width="220">Natuurgebied(en):</td>

			<td>&nbsp;</td>
		</tr>
		<tr>
			<td rowspan="2" height="110">&nbsp;</td>
			<td></td>
			<td rowspan="2" width="249" valign="top">
				<select NAME="sel_NatGeb" style="width:100%;" size="6" Multiple>
            	</select>
			</td>

		</tr>
		<tr>
			<td height="70" valign="top">
				<html:select style="width:100%;" property="sel_Beheereenheden" onclick="jsc_VulSelectUitArray(this.form.sel_NatGeb, arr_NatGeb[this.selectedIndex]);">
					<html:option value="Kennemerland">Kennemerland</html:option>
					<html:option value="Nieuwkoop">Nieuwkoop</html:option>
					<html:option value="Oost-Veluwe">Oost-Veluwe</html:option>
					<html:option value="Vechtplassen">Vechtplassen</html:option>
					<html:option value="West-Brabant">West-Brabant</html:option>
					<html:option value="Zuid-Limburg">Zuid-Limburg</html:option>
				</html:select>
			</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">

			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
	
	
	<table width ="500"  bgcolor="#CCCC99" border="0" cellspacing="0">
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied"value="Eenheid" onclick="jsc_optie1();"/>

			</td>
			<td width="220">Eenheid / Regio / Provincie:</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td rowspan="2" height="110">&nbsp;</td>
			<td></td>
			<td rowspan="2" width="249" valign="top">

				<select NAME="sel_Areaal" style="width:100%;" size="6">
            	</select>
			</td>
		</tr>
		<tr>
			<td height="70" valign="top">
				<html:select style="width:100%;" property="sel_gebieden" onclick="jsc_VulSelectUitArray(this.form.sel_Areaal, arr_Areaal[this.selectedIndex]);">
					<html:option value="Eenheid">Eenheid</html:option>
					<html:option value="Provincie">Provincie</html:option>
					<html:option value="Regio">Regio</html:option>
				</html:select>
			</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>

			<td></td>
		</tr>
	</table>


	<table width ="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="20" height="20" valign="top">
				<html:radio property="rad_Gebied" value="Nederland" onclick="jsc_optie2();"/>
			</td>

			<td width="220" valign="top">Nederland:</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>

	<table width="500" bgcolor="#CCCC99" border="0" cellspacing="0">	
		<tr>
			<td width="20">
				<html:radio property="rad_Gebied" value="Coordinaten" onclick="jsc_optie3();"/>
			</td>
			<td colspan="4">co�rdinaten:&nbsp;</td>
			<td>&nbsp;</td>
		</tr>

		<tr>
			<td>&nbsp;</td>
			<td width="150" align="right">linksonder X:&nbsp;</td>
			<td width="50">
              <html:text style="width:100%;" property="linksX" size="7"/>
			</td>
			<td width="50" align="right">Y:&nbsp;</td>
			<td width="50">
				<html:text style="width:100%;" property="linksY" size="7"/>
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td align="right">rechtsboven X:&nbsp;</td>
			<td>
             <html:text style="width:100%;" property="rechtsX" size="7"/>
			</td>
			<td align="right">Y:&nbsp;</td>
			<td>
			 <html:text style="width:100%;" property="rechtsY" size="7"/>    
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>

	<br>

	
	<table>
		<tr>
			<td width="450">
				Selecteer de gewenste kaart(en):
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">

				</a>
			</td>
		</tr> 
	</table>		
	

	<table width="500"  bgcolor="#CCCC00" border="0" cellspacing="0">
		<tr>
			<td width="96" align="left">kaarten:&nbsp;<br>klik hier voor vergroting en informatie</td>
			<td width="139">
				<img id="kartPicture" style="cursor:pointer" src="../../media/vastgoed/Nicolao%20Visscher.jpg" width="132" height="107" border="0" alt="Klik hier voor vergroting en meer gegevens van deze kaart" 
					onClick="javascript:small_window('kaart_popup.jsp');">

			</td>
			<td width="249">
				<html:select style="width:100%;" property="sel_Kaart" size="6" multiple="multiple" onclick="jsc_setPicture(this.selectedIndex);">
				</html:select>
			</td>
		</tr>
	</table>
	<br>
	
	
	<table>

		<tr>
			<td width="450">
				Geef de schaal of het formaat  en het aantal:
			</td>
			<td align="right">	
				<a href="#nowhere">
					<img src="../../media/vastgoed/Info.png" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">
				</a>
			</td>
		</tr> 
	</table>	

	
	<table width="500" border="0" cellspacing="0">

		<tr bgcolor="#CCCC99">
	  		<td width="20">
        		<html:radio property="rad_Schaal" value="schaal"/>
      		</td>
			<td width="100" align="right">schaal:&nbsp;</td>
      		<td width="100">
				<html:select style="width:100%;" property="schaal">
					<html:option value="1:500">1:500</html:option>
					<html:option value="1:1000">1:1000</html:option>
					<html:option  value="1:5000">1:5000</html:option>
				</html:select>
	  		</td>
      		<td width="200">&nbsp;</td>
      		<td>&nbsp;</td>
		</tr>
		<tr height="1" bgcolor="#FFFFFF">

			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
    	<tr bgcolor="#CCCC00">
			<td><html:radio property="rad_Schaal" value="formaat"/></td>
			<td align="right">formaat:&nbsp;</td>

      		<td>
				<html:select style="width:100%;" property="formaat">
					<html:option value="A4">A4</html:option>
					<html:option value="A3">A3</html:option>
					<html:option value="A2">A2</html:option>
					<html:option value="A1">A1</html:option>
					<html:option value="A0">A0</html:option>

      			</html:select>
			</td>
			<td align="right">aantal:&nbsp;</td>
      		<td><html:text property="aantal" size="4"/></td>
    	</tr>
		<tr height="10" bgcolor="#FFFFFF">
			<td></td>
			<td></td>

			<td></td>
			<td></td>
			<td></td>
		</tr>
        <tr bgcolor="#CCCC99">
            <td>
				<html:radio property="rad_Gevouwen" value="gevouwen"/>
			</td>
			<td align="left">&nbsp;gevouwen&nbsp;</td>

			<td></td>
			<td></td>
			<td></td>
        </tr>
		<tr height="1" bgcolor="#FFFFFF">
			<td></td>
			<td></td>
			<td></td>
			<td></td>

			<td></td>
		</tr>
        <tr bgcolor="#CCCC00">
            <td><html:radio property="rad_Gevouwen" value="opgerold"/></td>
			<td align="left">&nbsp;opgerold&nbsp;</td>
			<td></td>
			<td></td>
			<td></td>

        </tr>
  </table>
	
	<br>

	<table>
		<tr>
			<td width="450">
				Opmerkingen:
			</td>
			<td align="right">	
				<a href="#nowhere">

					<img src="../../media/vastgoed/Info.png" align="right" width="29" height="24" border=0 
						alt="Uitleg over dit invoer deel van het
formulier.">
				</a>
			</td>
		</tr> 
	</table>	

	<table width="500" bgcolor="#CCCC66" border="0" cellspacing="0">
		<tr height="5">
			<td width="5"></td>
			<td width="440"></td>
			<td width="50"></td>

			<td width="5"></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
        		<html:textarea property="opmerkingen" style="width:486px;" rows="5"></html:textarea>
      		</td>
			<td></td>

 		</tr>
 		<tr height="5">
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr height="50">
	  		<td></td>

			<td>toevoegen aan mijn bestelling:&nbsp;</td>
			<td align="right">
			<input type="image" src="../../media/vastgoed/wwagen.jpg" name="send"/>
			</td>
			<td></td>
		</tr>
		<tr height="5">
			<td></td>
			<td>
		
		<html:link 
        page="/nmintra/includes/vastgoed/KaartenAction.eb?shopping_cart">
        TERUG NAAR KART...
</html:link>

		</td>
			<td></td>

			<td></td>
		</tr>
	</table>
 
<input type="hidden" name="number" value="<%=request.getParameter("number")%>"/>
 
</html:form>

</body>
</html>
</mm:cloud>