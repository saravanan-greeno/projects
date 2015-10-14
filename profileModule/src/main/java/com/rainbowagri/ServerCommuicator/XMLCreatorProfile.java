/*
 * @Application Name : Live Prices
 * @Developer Name   : Ramya.D
 * @Date             : 24-09-2014
 */

package com.rainbowagri.ServerCommuicator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.rainbowagri.data.ProfilePageData;

public class XMLCreatorProfile {
	/**
	 * Serializes the Add Commodity screen's detail as String
	 * in XML format 
	 * 
	 * @param mUser
	 * @return xmlString
	 */
	public String serializeAddCommodity(ProfilePageData profilePageData) {
		String xmlString = null;

//<saveProfile>
//<profileId>0</profileId>
//<coordinatorUniqueId>5</coordinatorUniqueId>>
//<mobileNumber>9715447049</mobileNumber>
//<firstName>Sundhar</firstName>
//<lastName>Hari</lastName>
//		shopName
//<appName>BC/LP</appName>
//<pincode>625515</pincode>
//<description>Greeno is the combination of two words. Green + Innovation = Greeno</description>
//
//</saveProfile>
		String headTag = "";
		
		/*if(addCommodityData.getCommodityRefId() == 0)
			headTag = "savePriceDetails";
		else
			headTag = "editPriceDetails";*/
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element saveProfileE = document.createElement("saveProfile");
			document.appendChild(saveProfileE);

			Element profileIdE = document.createElement("profileId");
			saveProfileE.appendChild(profileIdE);
			profileIdE.appendChild(document.createTextNode(""+profilePageData.getProfileId()));

			
			Element coordinatorUniqueIdE = document.createElement("coordinatorUniqueId");
			saveProfileE.appendChild(coordinatorUniqueIdE);
			coordinatorUniqueIdE.appendChild(document.createTextNode(""+profilePageData.getCoordinatorUniqueId()));

			Element mobileNumberE = document.createElement("mobileNumber");
			saveProfileE.appendChild(mobileNumberE);
			mobileNumberE.appendChild(document.createTextNode(profilePageData.getMobileNumber()));
			
			Element firstNameE = document.createElement("firstName");
			saveProfileE.appendChild(firstNameE);
			firstNameE.appendChild(document.createTextNode(profilePageData.getFirstName()));

			
			Element lastNameE = document.createElement("lastName");
			saveProfileE.appendChild(lastNameE);
			lastNameE.appendChild(document.createTextNode(profilePageData.getLastName()));
			
			
			Element shopNameE = document.createElement("shopName");
			saveProfileE.appendChild(shopNameE);
			shopNameE.appendChild(document.createTextNode(profilePageData.getShopName()));
			
			Element appNameE = document.createElement("appName");
			saveProfileE.appendChild(appNameE);
			appNameE.appendChild(document.createTextNode(profilePageData.getAppName()));
			
			Element pincodeE = document.createElement("pincode");
			saveProfileE.appendChild(pincodeE);
			pincodeE.appendChild(document.createTextNode(""+profilePageData.getPincode()));

			Element minimumOrder = document.createElement("minOrder");
			saveProfileE.appendChild(minimumOrder);
			minimumOrder.appendChild(document.createTextNode(profilePageData.getMinimumOrder().toString()));
			
			Element selectedCategory = document.createElement("categoryList");
			saveProfileE.appendChild(selectedCategory);
			selectedCategory.appendChild(document.createTextNode(profilePageData.getSelectedCategories().toString()));
			
			Element deliveryPincodeValue = document.createElement("deliveryPincode");
			saveProfileE.appendChild(deliveryPincodeValue);
			deliveryPincodeValue.appendChild(document.createTextNode(profilePageData.getDeliveryLocation().toString()));
			
			Element descriptionE = document.createElement("description");
			saveProfileE.appendChild(descriptionE);
			descriptionE.appendChild(document.createTextNode(""+profilePageData.getDescription()));

			Element addressE = document.createElement("address");
			saveProfileE.appendChild(addressE);
			addressE.appendChild(document.createTextNode(""+profilePageData.getAddress()));

			Element cityE = document.createElement("city");
			saveProfileE.appendChild(cityE);
			cityE.appendChild(document.createTextNode(""+profilePageData.getCity()));
			
			Element districtE = document.createElement("district");
			saveProfileE.appendChild(districtE);
			districtE.appendChild(document.createTextNode(""+profilePageData.getDistrict()));

			Element geoLocationE = document.createElement("geoLocation");
			saveProfileE.appendChild(geoLocationE);
			geoLocationE.appendChild(document.createTextNode(""+profilePageData.getGeoLocation()));
		
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(document.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			xmlString = output.toString();

		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
		System.out.println("=== Add Commodity Request XML ==="+xmlString);
		return xmlString;
	}
	
	/**
	 * Serialize sign up request argument string
	 */
	public String serializeSignUpArgs(ProfilePageData mData) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("reqSignup");
			document.appendChild(userAuth);
			
			Element profileId = document.createElement("profileId");
			userAuth.appendChild(profileId);
			profileId.appendChild(document.createTextNode(mData.getProfileId().toString()));
			
			Element coordinatorUniqueId = document.createElement("coordinatorUniqueId");
			userAuth.appendChild(coordinatorUniqueId);
			coordinatorUniqueId.appendChild(document.createTextNode(mData.getCoordinatorUniqueId().toString()));

			Element mobileNo = document.createElement("mobileNumber");
			userAuth.appendChild(mobileNo);
			mobileNo.appendChild(document.createTextNode(mData.getMobileNumber().toString()));

			Element firstName = document.createElement("firstName");
			userAuth.appendChild(firstName);
			firstName.appendChild(document.createTextNode(mData.getFirstName().toString()));
			
			Element lastName = document.createElement("lastName");
			userAuth.appendChild(lastName);
			lastName.appendChild(document.createTextNode(mData.getLastName().toString()));
			
			Element address = document.createElement("address");
			userAuth.appendChild(address);
			address.appendChild(document.createTextNode(mData.getAddress().toString()));
			
			Element city = document.createElement("city");
			userAuth.appendChild(city);
			city.appendChild(document.createTextNode(mData.getCity().toString()));
			
			Element geoLocation = document.createElement("geoLocation");
			userAuth.appendChild(geoLocation);
			geoLocation.appendChild(document.createTextNode(mData.getGeoLocation().toString()));
			
			Element appNameElement = document.createElement("appName");
			userAuth.appendChild(appNameElement);
			appNameElement.appendChild(document.createTextNode(mData.getAppName().toString()));
			
			Element shopName = document.createElement("shopName");
			userAuth.appendChild(shopName);
			shopName.appendChild(document.createTextNode(mData.getShopName().toString()));
			
			Element district = document.createElement("district");
			userAuth.appendChild(district);
			district.appendChild(document.createTextNode(mData.getDistrict().toString()));
			
			Element pincode = document.createElement("pincode");
			userAuth.appendChild(pincode);
			pincode.appendChild(document.createTextNode(mData.getPincode().toString()));
			
			Element minimumOrder = document.createElement("minOrder");
			userAuth.appendChild(minimumOrder);
			minimumOrder.appendChild(document.createTextNode(mData.getMinimumOrder().toString()));
			
			Element selectedCategory = document.createElement("categoryList");
			userAuth.appendChild(selectedCategory);
			selectedCategory.appendChild(document.createTextNode(mData.getSelectedCategories().toString()));
			
			Element deliveryPincodeValue = document.createElement("deliveryPincode");
			userAuth.appendChild(deliveryPincodeValue);
			deliveryPincodeValue.appendChild(document.createTextNode(mData.getDeliveryLocation().toString()));
			
			Element profileTypeValue = document.createElement("profileType");
			userAuth.appendChild(profileTypeValue);
			profileTypeValue.appendChild(document.createTextNode(mData.getProfileType().toString()));
			
			Element deliveryDistrictValue = document.createElement("deliveryDistrict");
			userAuth.appendChild(deliveryDistrictValue);
			deliveryDistrictValue.appendChild(document.createTextNode(mData.getDeliveryDistrict().toString()));
			
			Element description = document.createElement("description");
			userAuth.appendChild(description);
			description.appendChild(document.createTextNode(mData.getDescription().toString()));

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(document.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			xmlString = output.toString();

		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
		return xmlString;
	}
	
	public String serializeViewProfile(ProfilePageData profilePageData) {
		String xmlString = null;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element saveProfileE = document.createElement("viewProfile");
			document.appendChild(saveProfileE);


			
			Element coordinatorUniqueIdE = document.createElement("coordinatorUniqueId");
			saveProfileE.appendChild(coordinatorUniqueIdE);
			coordinatorUniqueIdE.appendChild(document.createTextNode(""+profilePageData.getCoordinatorUniqueId()));

			Element appNameE = document.createElement("appName");
			saveProfileE.appendChild(appNameE);
			if(profilePageData.getAppName().startsWith("LivePriceUser"))
				appNameE.appendChild(document.createTextNode("LivePrice"));
			else
				appNameE.appendChild(document.createTextNode(profilePageData.getAppName()));
			
			
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(document.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			xmlString = output.toString();

		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
		System.out.println("=== Add Commodity Request XML ==="+xmlString);
		return xmlString;
	}

	public String serializeViewProfilePicture(String idString) {
		String xmlString = null;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewProfileImageE = document.createElement("viewProfileImage");
			document.appendChild(viewProfileImageE);


			
			Element idE = document.createElement("id");
			viewProfileImageE.appendChild(idE);
			idE.appendChild(document.createTextNode(idString));


			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Properties outFormat = new Properties();
			outFormat.setProperty(OutputKeys.INDENT, "yes");
			outFormat.setProperty(OutputKeys.METHOD, "xml");
			outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			outFormat.setProperty(OutputKeys.VERSION, "1.0");
			outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperties(outFormat);
			DOMSource domSource = new DOMSource(document.getDocumentElement());
			OutputStream output = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(output);
			transformer.transform(domSource, result);
			xmlString = output.toString();

		} catch (ParserConfigurationException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
		
		return xmlString;
	}
		
}
