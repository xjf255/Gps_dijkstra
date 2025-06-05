package org.example.gps.View;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.gps.Utils.DestinationTypes; // Asegúrate que la ruta sea correcta

import java.util.EnumMap;
import java.util.Map;

public class VisualVertex {
    private int id;
    private String nombre;
    private DestinationTypes type;
    private double altitude;

    public double posX;
    public double posY;

    public static double radius = 15;
    public static boolean displayId = true;
    public static boolean displayAltitude = true;

    private Color vColor;
    private Color originalColor;
    private Color borderColor = Color.WHITE;

    private static final Map<DestinationTypes, Color> TYPE_COLOR_MAP = new EnumMap<>(DestinationTypes.class);
    private static final Color DEFAULT_TYPE_COLOR = Color.rgb(120, 120, 120);

    static {
        TYPE_COLOR_MAP.put(DestinationTypes.RESTAURANT, Color.ORANGE);
        TYPE_COLOR_MAP.put(DestinationTypes.GROUNDS, Color.FORESTGREEN);
        TYPE_COLOR_MAP.put(DestinationTypes.PARKING, Color.SLATEGRAY);
        TYPE_COLOR_MAP.put(DestinationTypes.MALL, Color.DEEPPINK);
        TYPE_COLOR_MAP.put(DestinationTypes.SCHOOL, Color.DODGERBLUE);
        TYPE_COLOR_MAP.put(DestinationTypes.HOSPITAL, Color.CRIMSON);
    }

    public VisualVertex(int id, String nombre, double altitude, DestinationTypes type) {
        this.id = id;
        this.nombre = nombre;
        this.altitude = altitude;
        this.type = type;
        this.originalColor = TYPE_COLOR_MAP.getOrDefault(this.type, DEFAULT_TYPE_COLOR);
        this.vColor = this.originalColor;
    }

    public int getId() { return this.id; }
    public String getNombre() { return this.nombre; }
    public DestinationTypes getType() { return this.type; }
    public double getAltitude() { return this.altitude; }

    public void setColor(Color c) { this.vColor = c; }
    public void setDefaultColor() { this.vColor = this.originalColor; }
    public void setBorderColor(Color color) { this.borderColor = color; }
    public void setPos(double posX, double posY) { this.posX = posX; this.posY = posY; }

    public String getTooltipText() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id);
        if (nombre != null && !nombre.isEmpty()) sb.append("\nNombre: ").append(nombre);
        if (type != null) sb.append("\nTipo: ").append(type.toString());
        sb.append("\nAltitud: ").append(String.format("%.0fm", altitude));
        return sb.toString();
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.arc(posX, posY, radius, radius, 0, 360);
        gc.closePath();
        gc.setStroke(borderColor);
        gc.setLineWidth(2);
        gc.stroke();
        gc.setFill(vColor);
        gc.fill();

        double textOffsetY = 0;
        if (displayId) {
            gc.setFill(Color.WHITE);
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", 11));
            gc.fillText(String.valueOf(id), posX, posY);
            textOffsetY = VisualVertex.radius * 0.75;
        }
        if (displayAltitude) {
            gc.setFill(Color.LIGHTSKYBLUE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Arial", 9));
            if (displayId) {
                gc.setTextBaseline(VPos.TOP);
                gc.fillText(String.format("%.0fm", altitude), posX, posY + textOffsetY + 2);
            } else {
                gc.setTextBaseline(VPos.CENTER);
                gc.fillText(String.format("%.0fm", altitude), posX, posY);
            }
        }
    }

    @Override
    public String toString() { return String.valueOf(this.id) + (nombre != null ? " - " + nombre : "") + (type != null ? " (" + type.name() + ")" : ""); }
    @Override
    public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; VisualVertex that = (VisualVertex) o; return id == that.id; }
    @Override
    public int hashCode() { return Integer.hashCode(id); }
}