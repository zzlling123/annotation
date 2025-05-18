package com.xinkao.erp.exercise.utils.jx2d;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment {
    private String key;
    private String id;
    private double start;
    private double end;
    private String label;
    private String content;
    private String role;
    private String text;

    // Getter 和 Setter 方法（可自动生成）

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getStart() { return start; }
    public void setStart(double start) { this.start = start; }

    public double getEnd() { return end; }
    public void setEnd(double end) { this.end = end; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @Override
    public String toString() {
        return "Segment{" +
                "key='" + key + '\'' +
                ", id='" + id + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", label='" + label + '\'' +
                ", content='" + content + '\'' +
                ", role='" + role + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
