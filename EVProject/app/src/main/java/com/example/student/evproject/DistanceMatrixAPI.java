package com.example.student.evproject;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class DistanceMatrixAPI {

    private String serviceKey = "AIzaSyBewJIA8-Js4yuDabIbvloyvFZV7uapk0g";
    public String[] data = new String[2];    // [0]:duration,[1]:distance

    public String[] request(Pos src, Pos dst) throws Exception {

        StringBuilder urlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/xml");
        urlBuilder.append("?" + URLEncoder.encode("origins", "UTF-8") + "=" + Double.toString(src.lat) + ","
                + Double.toString(src.lng));
        urlBuilder.append("&" + URLEncoder.encode("destinations", "UTF-8") + "=" + Double.toString(dst.lat) + ","
                + Double.toString(dst.lng));
        urlBuilder.append("&" + URLEncoder.encode("language", "UTF-8") + "=" + URLEncoder.encode("ko", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("key", "UTF-8") + "=" + serviceKey);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String result = "";
        String line;

        while ((line = br.readLine()) != null) {
            result = result + line.trim();
        }

        InputSource is = new InputSource(new StringReader(result));
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(is);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expr = xpath.compile("//element/duration");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList child = nodeList.item(i).getChildNodes();
            for (int j = 0; j < child.getLength(); j++) {
                Node node = child.item(j);
                if (node.getNodeName().equals("text"))
                    data[0] = node.getTextContent();
            }
        }

        expr = xpath.compile("//element/distance");
        nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList child = nodeList.item(i).getChildNodes();
            for (int j = 0; j < child.getLength(); j++) {
                Node node = child.item(j);
                if (node.getNodeName().equals("text"))
                    data[1] = node.getTextContent();
            }
        }

        br.close();
        conn.disconnect();

        return data;
    }
}
