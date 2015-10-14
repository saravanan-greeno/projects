/**
 *  Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * @Application Name : Live Prices
 * @Developer Name   : Ramya.D & Saravanan & sellathurai
 * @Date             : 24-09-2014
 */
package com.rainbowagri.servercommunicator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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

import com.rainbowagri.data.AddCommodityData;
import com.rainbowagri.data.RowItem;
import com.rainbowagri.data.Vendor;
 
public class XMLCreator {
	/**
	 * Serializes the Add Commodity screen's detail as String in XML format
	 * 
	 * @param mUser
	 * @return xmlString
	 */
	public String serializeAddCommodity(AddCommodityData addCommodityData) {
		String xmlString = null;

		String headTag = "";

		if (addCommodityData.getCommodityRefId() == 0)
			headTag = "savePriceDetails";
		else
			headTag = "savePriceDetails";
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element savePriceDetailsE = document.createElement(headTag);
			document.appendChild(savePriceDetailsE);

			Element refIdE = document.createElement("coordinatorRefId");
			savePriceDetailsE.appendChild(refIdE);
			refIdE.appendChild(document.createTextNode(""
					+ addCommodityData.getCoordinatorRefId()));

			Element comRefId = document.createElement("commodityRefId");
			savePriceDetailsE.appendChild(comRefId);
			comRefId.appendChild(document.createTextNode(""
					+ addCommodityData.getCommodityRefId()));
			
			Element mainCategoryValue = document.createElement("mainCategory");
			savePriceDetailsE.appendChild(mainCategoryValue);
			mainCategoryValue.appendChild(document.createTextNode(addCommodityData
					.getMainCategory()));

			Element categoryE = document.createElement("category");
			savePriceDetailsE.appendChild(categoryE);
			categoryE.appendChild(document.createTextNode(addCommodityData
					.getCategory()));

			Element commodityNameE = document.createElement("commodityName");
			savePriceDetailsE.appendChild(commodityNameE);
			commodityNameE.appendChild(document.createTextNode(addCommodityData
					.getCommodityName()));

			Element priceE = document.createElement("price");
			savePriceDetailsE.appendChild(priceE);
			priceE.appendChild(document.createTextNode(addCommodityData
					.getPrice()));

			Element unitsE = document.createElement("unit");
			savePriceDetailsE.appendChild(unitsE);
			unitsE.appendChild(document.createTextNode(addCommodityData
					.getUnits()));

			Element lastUpdatedDateE = document
					.createElement("lastUpdatedDate");
			savePriceDetailsE.appendChild(lastUpdatedDateE);
			lastUpdatedDateE.appendChild(document
					.createTextNode(addCommodityData.getLastUpdatedDate()));

			Element mimimumQnty = document.createElement("minQty");
			savePriceDetailsE.appendChild(mimimumQnty);
			mimimumQnty.appendChild(document.createTextNode(""
					+ addCommodityData.getMinimumQuantity()));

			Element quantityE = document.createElement("quantity");
			savePriceDetailsE.appendChild(quantityE);
			quantityE.appendChild(document.createTextNode(""
					+ addCommodityData.getQuantity()));

			Element discountE = document.createElement("discount");
			savePriceDetailsE.appendChild(discountE);
			discountE.appendChild(document.createTextNode(""
					+ addCommodityData.getDiscounts()));

			Element minUnitValue = document.createElement("minUnit");
			savePriceDetailsE.appendChild(minUnitValue);
			minUnitValue.appendChild(document.createTextNode(""
					+ addCommodityData.getMinQtyUnit()));

			Element comInfo = document.createElement("description");
			savePriceDetailsE.appendChild(comInfo);
			comInfo.appendChild(document.createTextNode(""
					+ addCommodityData.getCommodityInfo()));

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

	/**
	 * Serializes the user authentication details (Login information) as String
	 * in XML format Serializing means preparing in the required format
	 * 
	 * @param mUser
	 * @return xmlString
	 */
	public String serializeForDeleteCommodity(String comId, String vendorIds,
			String comNme, String varietyName) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("deletePriceDetails");
			document.appendChild(userAuth);

			Element comeId = document.createElement("id");
			userAuth.appendChild(comeId);
			comeId.appendChild(document.createTextNode("" + comId));

			Element vendorId = document.createElement("vendorId");
			userAuth.appendChild(vendorId);
			vendorId.appendChild(document.createTextNode("" + vendorIds));

			Element cmNme = document.createElement("commodityName");
			userAuth.appendChild(cmNme);
			cmNme.appendChild(document.createTextNode("" + comNme));

			Element categoryName = document.createElement("categoryName");
			userAuth.appendChild(categoryName);
			categoryName.appendChild(document.createTextNode(varietyName));

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

	/**
	 * xml file for editing the price details of commoditites
	 */

	public String serializePriceEditForCommodities(ArrayList<RowItem> items,
			String vendorId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element checkoutOrder = document
					.createElement("MultipleCommodityEditPrice");
			document.appendChild(checkoutOrder);

			Element addressId = document.createElement("vendorId");
			checkoutOrder.appendChild(addressId);
			addressId.appendChild(document.createTextNode(vendorId));

			for (int i = 0; i < items.size(); i++) {

				Element commodities = document.createElement("Commodity");
				checkoutOrder.appendChild(commodities);

				Element Commodityid = document.createElement("commodityId");
				commodities.appendChild(Commodityid);
				Commodityid.appendChild(document.createTextNode(items.get(i)
						.getId()));

				Element price = document.createElement("price");
				commodities.appendChild(price);
				price.appendChild(document.createTextNode(items.get(i)
						.getPrice()));

			}

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

	/**
	 * xml file for viewing the price details of commoditites for edit
	 */
	public String serializeViewCommodityPriceForEdit(String vendorId,
			String categoryName) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element userAuth = document.createElement("CategoryWiseCommodity");
			document.appendChild(userAuth);

			Element vendorid = document.createElement("vendorId");
			userAuth.appendChild(vendorid);
			vendorid.appendChild(document.createTextNode(vendorId));

			Element categoryname = document.createElement("categoryName");
			userAuth.appendChild(categoryname);
			categoryname.appendChild(document.createTextNode(categoryName));

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

	/**
	 * xml file for editing the commoditites
	 */

	public String serializeForEditCommodity(String oldCategory,
			String newCategory, String vendorId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element editCategory = document.createElement("editCategory");
			document.appendChild(editCategory);

			Element vedorId = document.createElement("vendorId");
			editCategory.appendChild(vedorId);
			vedorId.appendChild(document.createTextNode(vendorId));

			Element oldCategoryName = document.createElement("oldCategoryName");
			editCategory.appendChild(oldCategoryName);
			oldCategoryName.appendChild(document.createTextNode(oldCategory));

			Element newCategoryName = document.createElement("newCategoryName");
			editCategory.appendChild(newCategoryName);
			newCategoryName.appendChild(document.createTextNode(newCategory));

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

	/**
	 * xml file for know the order status process
	 */
	public String serializeOrderStatuss(String orderID, String status) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewPriceDetails = document.createElement("orderStatus");
			document.appendChild(viewPriceDetails);

			Element vendorIdValue = document.createElement("orderId");
			viewPriceDetails.appendChild(vendorIdValue);
			vendorIdValue.appendChild(document.createTextNode(orderID));

			Element StatusValues = document.createElement("status");
			viewPriceDetails.appendChild(StatusValues);
			StatusValues.appendChild(document.createTextNode(status));

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

	/**
	 * xml file for activate or deactivate the shop details
	 */

	public String serializeShopActiveOrInactive(String status, String venId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element activeShop = document.createElement("activeShop");
			document.appendChild(activeShop);

			Element shopstatus = document.createElement("active");
			activeShop.appendChild(shopstatus);
			shopstatus.appendChild(document.createTextNode("" + status));

			Element shopIdtoactive = document.createElement("vendorId");
			activeShop.appendChild(shopIdtoactive);
			shopIdtoactive.appendChild(document.createTextNode("" + venId));

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

	/**
	 * xml file for TODO commoditites
	 */

	public String serializePriceTODO(String commodityId, String vendorId,
			String content) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element hidePrice = document.createElement("hidePrice");
			document.appendChild(hidePrice);

			Element vedorId = document.createElement("vendorId");
			hidePrice.appendChild(vedorId);
			vedorId.appendChild(document.createTextNode(vendorId));

			Element comId = document.createElement("commodityId");
			hidePrice.appendChild(comId);
			comId.appendChild(document.createTextNode(commodityId));

			Element priceHideOrShow = document.createElement("toDo");
			hidePrice.appendChild(priceHideOrShow);
			priceHideOrShow.appendChild(document.createTextNode(content));

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

	/**
	 * xml file for viewing the commodity details
	 */
	public String serializeCommodityDetails(String vendId, String pageNo) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewPriceDetails = document
					.createElement("viewPriceDetails");
			document.appendChild(viewPriceDetails);

			Element vendorId = document.createElement("vendorId");
			viewPriceDetails.appendChild(vendorId);
			vendorId.appendChild(document.createTextNode(vendId));
			Element pno = document.createElement("pageNo");
			viewPriceDetails.appendChild(pno);
			pno.appendChild(document.createTextNode(pageNo));

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

	/**
	 * xml file forsearching the details of commoditites
	 */
	public String serializeCommodityDetailsForSearch(String vendId,
			String pageNo, String searchContent, String filterContent) {
		String xmlString = null;
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewPriceDetails = document
					.createElement("viewPriceDetails");
			document.appendChild(viewPriceDetails);

			Element vendorId = document.createElement("vendorId");
			viewPriceDetails.appendChild(vendorId);
			vendorId.appendChild(document.createTextNode(vendId));

			Element pno = document.createElement("pageNo");
			viewPriceDetails.appendChild(pno);
			pno.appendChild(document.createTextNode(pageNo));

			Element searchValue = document.createElement("search");
			viewPriceDetails.appendChild(searchValue);
			searchValue.appendChild(document.createTextNode(searchContent));

			Element filterValue = document.createElement("filter");
			viewPriceDetails.appendChild(filterValue);
			filterValue.appendChild(document.createTextNode(filterContent));

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

	/**
	 * xml file for fetching the order status of commoditites
	 */
	public String fetchOrderStatus(String orderId) {
		String xmlString = null;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewDistrict = document.createElement("orderStatus");
			document.appendChild(viewDistrict);

			Element stateValue = document.createElement("orderId");
			viewDistrict.appendChild(stateValue);
			stateValue.appendChild(document.createTextNode(orderId));

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

	/**
	 * xml file for cancel the purchase orders
	 */

	public String cancelPurchaseOrders(String orderId) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element cancelOrder = document.createElement("cancelOrder");
			document.appendChild(cancelOrder);

			Element oId = document.createElement("orderId");
			cancelOrder.appendChild(oId);
			oId.appendChild(document.createTextNode(orderId));

			Element status = document.createElement("status");
			cancelOrder.appendChild(status);
			status.appendChild(document.createTextNode("Cancel"));

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

	/**
	 * xml file for getting categories from server
	 */

	public String createCategoryXML(String VendorID, String mainCategory) {
		String xmlString = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = documentBuilder.newDocument();

			Element viewCommodity = document.createElement("getSubCategory");
			document.appendChild(viewCommodity);

			/*Element shopId = document.createElement("shopId");
			viewCommodity.appendChild(shopId);
			shopId.appendChild(document.createTextNode(shopIds));*/

			Element vendorID = document.createElement("vendorId");
			viewCommodity.appendChild(vendorID);
			vendorID.appendChild(document.createTextNode(VendorID));
			
			Element mainCategoryValue = document.createElement("mainCategory");
			viewCommodity.appendChild(mainCategoryValue);
			mainCategoryValue.appendChild(document.createTextNode(mainCategory));

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
