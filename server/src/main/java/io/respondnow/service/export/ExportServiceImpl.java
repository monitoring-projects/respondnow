package io.respondnow.service.export;

import io.respondnow.model.incident.Incident;
import io.respondnow.model.incident.Role;
import io.respondnow.model.incident.Timeline;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

  private static final DateTimeFormatter DATE_FORMATTER = 
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  @Override
  public String exportToCSV(List<Incident> incidents) {
    if (incidents == null || incidents.isEmpty()) {
      return "";
    }

    StringBuilder csv = new StringBuilder();
    
    // CSV Header
    csv.append("ID,Name,Severity,Status,Type,Summary,Description,Created By,Created At,Updated At,Incident URL,Tags,Duration (minutes)\n");

    for (Incident incident : incidents) {
      csv.append(escapeCsvValue(incident.getIdentifier())).append(",");
      csv.append(escapeCsvValue(incident.getName())).append(",");
      csv.append(escapeCsvValue(incident.getSeverity() != null ? incident.getSeverity().toString() : "")).append(",");
      csv.append(escapeCsvValue(incident.getStatus() != null ? incident.getStatus().toString() : "")).append(",");
      csv.append(escapeCsvValue(incident.getType() != null ? incident.getType().toString() : "")).append(",");
      csv.append(escapeCsvValue(incident.getSummary())).append(",");
      csv.append(escapeCsvValue(incident.getDescription())).append(",");
      csv.append(escapeCsvValue(getCreatedByName(incident))).append(",");
      csv.append(escapeCsvValue(formatTimestamp(incident.getCreatedAt()))).append(",");
      csv.append(escapeCsvValue(formatTimestamp(incident.getUpdatedAt()))).append(",");
      csv.append(escapeCsvValue(incident.getIncidentUrl())).append(",");
      csv.append(escapeCsvValue(formatTags(incident.getTags()))).append(",");
      csv.append(calculateDuration(incident));
      csv.append("\n");
    }

    return csv.toString();
  }

  @Override
  public byte[] exportToPDF(Incident incident) {
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);
      
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        float margin = 50;
        float yPosition = page.getMediaBox().getHeight() - margin;
        float fontSize = 12;
        float leading = 1.5f * fontSize;
        
        // Title
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("INCIDENT DETAILS");
        contentStream.endText();
        yPosition -= leading * 2;
        
        // Content
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        
        yPosition = writeTextLine(contentStream, "ID: " + nullSafe(incident.getIdentifier()), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Name: " + nullSafe(incident.getName()), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Severity: " + (incident.getSeverity() != null ? incident.getSeverity().toString() : "N/A"), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Status: " + (incident.getStatus() != null ? incident.getStatus().toString() : "N/A"), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Type: " + (incident.getType() != null ? incident.getType().toString() : "N/A"), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Summary: " + nullSafe(incident.getSummary()), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Created By: " + getCreatedByName(incident), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Created At: " + formatTimestamp(incident.getCreatedAt()), margin, yPosition, leading);
        yPosition = writeTextLine(contentStream, "Duration: " + calculateDuration(incident) + " minutes", margin, yPosition, leading);
        
        yPosition -= leading;
        
        // Key Members
        if (incident.getRoles() != null && !incident.getRoles().isEmpty()) {
          contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
          yPosition = writeTextLine(contentStream, "KEY MEMBERS:", margin, yPosition, leading);
          contentStream.setFont(PDType1Font.HELVETICA, fontSize);
          
          for (Role role : incident.getRoles()) {
            String memberName = role.getUserDetails() != null 
                ? (role.getUserDetails().getName() != null ? role.getUserDetails().getName() : role.getUserDetails().getUserName())
                : "Unknown";
            yPosition = writeTextLine(contentStream, "  - " + memberName + " (" + role.getRoleType() + ")", margin, yPosition, leading);
          }
          yPosition -= leading;
        }
        
        // Timeline
        if (incident.getTimelines() != null && !incident.getTimelines().isEmpty()) {
          contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
          yPosition = writeTextLine(contentStream, "TIMELINE:", margin, yPosition, leading);
          contentStream.setFont(PDType1Font.HELVETICA, fontSize);
          
          int count = 0;
          for (Timeline timeline : incident.getTimelines()) {
            if (count >= 10) break; // Limit to first 10 timeline entries to fit on page
            String time = formatTimestamp(timeline.getCreatedAt());
            String type = timeline.getType() != null ? timeline.getType().toString().replace("_", " ") : "Event";
            yPosition = writeTextLine(contentStream, "  " + time + " - " + type, margin, yPosition, leading);
            count++;
          }
        }
      }
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      document.save(baos);
      return baos.toByteArray();
      
    } catch (IOException e) {
      throw new RuntimeException("Error generating PDF", e);
    }
  }
  
  private float writeTextLine(PDPageContentStream contentStream, String text, float x, float y, float leading) throws IOException {
    contentStream.beginText();
    contentStream.newLineAtOffset(x, y);
    contentStream.showText(text);
    contentStream.endText();
    return y - leading;
  }

  @Override
  public byte[] exportToPDF(List<Incident> incidents) {
    try (PDDocument document = new PDDocument()) {
      float margin = 50;
      float fontSize = 10;
      float leading = 1.5f * fontSize;
      
      for (int i = 0; i < incidents.size(); i++) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        Incident incident = incidents.get(i);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
          float yPosition = page.getMediaBox().getHeight() - margin;
          
          // Page header
          contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
          contentStream.beginText();
          contentStream.newLineAtOffset(margin, yPosition);
          contentStream.showText("Incident " + (i + 1) + " of " + incidents.size());
          contentStream.endText();
          yPosition -= leading * 2;
          
          // Incident details
          contentStream.setFont(PDType1Font.HELVETICA, fontSize);
          yPosition = writeTextLine(contentStream, "ID: " + nullSafe(incident.getIdentifier()), margin, yPosition, leading);
          yPosition = writeTextLine(contentStream, "Name: " + nullSafe(incident.getName()), margin, yPosition, leading);
          yPosition = writeTextLine(contentStream, "Severity: " + (incident.getSeverity() != null ? incident.getSeverity().toString() : "N/A"), margin, yPosition, leading);
          yPosition = writeTextLine(contentStream, "Status: " + (incident.getStatus() != null ? incident.getStatus().toString() : "N/A"), margin, yPosition, leading);
          yPosition = writeTextLine(contentStream, "Created At: " + formatTimestamp(incident.getCreatedAt()), margin, yPosition, leading);
        }
      }
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      document.save(baos);
      return baos.toByteArray();
      
    } catch (IOException e) {
      throw new RuntimeException("Error generating PDF for incidents list", e);
    }
  }

  private String escapeCsvValue(String value) {
    if (value == null) {
      return "";
    }
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  private String formatTimestamp(Long timestamp) {
    if (timestamp == null) {
      return "";
    }
    return DATE_FORMATTER.format(Instant.ofEpochSecond(timestamp));
  }

  private String getCreatedByName(Incident incident) {
    if (incident.getCreatedBy() == null) {
      return "";
    }
    if (incident.getCreatedBy().getName() != null) {
      return incident.getCreatedBy().getName();
    }
    return incident.getCreatedBy().getUserName() != null ? incident.getCreatedBy().getUserName() : "";
  }

  private String formatTags(List<String> tags) {
    if (tags == null || tags.isEmpty()) {
      return "";
    }
    return String.join("; ", tags);
  }

  private long calculateDuration(Incident incident) {
    if (incident.getCreatedAt() == null) {
      return 0;
    }
    long endTime = incident.getStatus() != null && "Resolved".equals(incident.getStatus().toString()) 
        ? (incident.getUpdatedAt() != null ? incident.getUpdatedAt() : Instant.now().getEpochSecond())
        : Instant.now().getEpochSecond();
    return (endTime - incident.getCreatedAt()) / 60;
  }

  private String nullSafe(String value) {
    return value != null ? value : "N/A";
  }
}
