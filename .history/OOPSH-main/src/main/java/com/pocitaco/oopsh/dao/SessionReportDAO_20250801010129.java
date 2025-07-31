package com.pocitaco.oopsh.dao;

import com.pocitaco.oopsh.interfaces.CrudOperations;
import com.pocitaco.oopsh.models.SessionReport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionReportDAO extends BaseDAO<SessionReport, Integer> {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SessionReportDAO() {
        super("session_reports.xml", "sessionReport");
    }

    @Override
    protected SessionReport elementToEntity(Element element) {
        SessionReport report = new SessionReport();
        report.setId(Integer.parseInt(element.getAttribute("id")));
        report.setSessionId(Integer.parseInt(element.getElementsByTagName("sessionId").item(0).getTextContent()));
        report.setExaminerId(Integer.parseInt(element.getElementsByTagName("examinerId").item(0).getTextContent()));
        report.setReportContent(element.getElementsByTagName("reportContent").item(0).getTextContent());
        report.setNotes(element.getElementsByTagName("notes").item(0).getTextContent());
        
        try {
            String dateStr = element.getElementsByTagName("createdDate").item(0).getTextContent();
            report.setCreatedDate(DATE_FORMAT.parse(dateStr));
        } catch (Exception e) {
            report.setCreatedDate(new Date());
        }
        
        return report;
    }

    @Override
    protected void entityToElement(SessionReport report, Element element) {
        Document doc = element.getOwnerDocument();
        
        element.setAttribute("id", String.valueOf(report.getId()));
        
        Element sessionIdElement = doc.createElement("sessionId");
        sessionIdElement.setTextContent(String.valueOf(report.getSessionId()));
        element.appendChild(sessionIdElement);
        
        Element examinerIdElement = doc.createElement("examinerId");
        examinerIdElement.setTextContent(String.valueOf(report.getExaminerId()));
        element.appendChild(examinerIdElement);
        
        Element reportContentElement = doc.createElement("reportContent");
        reportContentElement.setTextContent(report.getReportContent());
        element.appendChild(reportContentElement);
        
        Element notesElement = doc.createElement("notes");
        notesElement.setTextContent(report.getNotes());
        element.appendChild(notesElement);
        
        Element createdDateElement = doc.createElement("createdDate");
        createdDateElement.setTextContent(DATE_FORMAT.format(report.getCreatedDate()));
        element.appendChild(createdDateElement);
    }

    @Override
    protected Integer getEntityId(SessionReport report) {
        return report.getId();
    }

    @Override
    public SessionReport save(SessionReport report) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            Element rootElement = doc.getDocumentElement();
            
            Element reportElement = doc.createElement(getElementName());
            entityToElement(report, reportElement);
            rootElement.appendChild(reportElement);
            
            saveDocument(doc);
            return report;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public SessionReport update(SessionReport report) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == report.getId()) {
                    // Clear existing children
                    while (element.hasChildNodes()) {
                        element.removeChild(element.getFirstChild());
                    }
                    entityToElement(report, element);
                    saveDocument(doc);
                    return report;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Integer id) {
        lock.writeLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == id) {
                    element.getParentNode().removeChild(element);
                    saveDocument(doc);
                    break;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<SessionReport> findById(Integer id) {
        lock.readLock().lock();
        try {
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (Integer.parseInt(element.getAttribute("id")) == id) {
                    return Optional.of(elementToEntity(element));
                }
            }
            return Optional.empty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<SessionReport> findAll() {
        lock.readLock().lock();
        try {
            List<SessionReport> reports = new ArrayList<>();
            Document doc = loadDocument();
            NodeList nodes = doc.getElementsByTagName(getElementName());
            for (int i = 0; i < nodes.getLength(); i++) {
                reports.add(elementToEntity((Element) nodes.item(i)));
            }
            return reports;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<SessionReport> getAll() {
        return findAll();
    }
}
